package ml.junheah.osuparser.replay;

import java.io.BufferedOutputStream;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import SevenZip.Compression.LZMA.Decoder;

public class Replay{
	List<ReplayFrame> frames;
	
	int mode, version, score, mods, dataLength;
	short best, good, bad, geki, katu, miss, combo;
	long ticks, id;
	Boolean perfect;
	String mapmd5, user, replaymd5, lifebar;
	
	
	public Replay(byte[] data) {
		parse(data);
	}
	
	String bytesToString(byte[] b) {
		//TODO: make this work
		
		return "";
	}
	
	int bytesToInt(byte[] b) {
		return ByteBuffer.wrap(b).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
	}

	short bytesToShort(byte[] b) {
		return ByteBuffer.wrap(b).order(java.nio.ByteOrder.LITTLE_ENDIAN).getShort();
	}
	
	long bytesToLong(byte[] b) {
		return ByteBuffer.wrap(b).order(java.nio.ByteOrder.LITTLE_ENDIAN).getLong();
	}
	
	void parse(byte[] data){
		int lengthtmp = 0;
		//get replay data
		int cursor = 0;
		
		mode = data[cursor++];
		version = bytesToInt(Arrays.copyOfRange(data, cursor, cursor+=4));
		cursor++;
		lengthtmp= data[cursor++];
		if(lengthtmp>0) mapmd5 = bytesToString(Arrays.copyOfRange(data,cursor,cursor+=lengthtmp));
		
		cursor++;
		lengthtmp= data[cursor++];
		if(lengthtmp>0) user = bytesToString(Arrays.copyOfRange(data,cursor,cursor+=lengthtmp));
		
		cursor++;
		lengthtmp= data[cursor++];
		if(lengthtmp>0) replaymd5 = bytesToString(Arrays.copyOfRange(data,cursor,cursor+=lengthtmp));
		
		best = bytesToShort(Arrays.copyOfRange(data,cursor,cursor+=2));
		good = bytesToShort(Arrays.copyOfRange(data,cursor,cursor+=2));
		bad = bytesToShort(Arrays.copyOfRange(data,cursor,cursor+=2));
		
		geki = bytesToShort(Arrays.copyOfRange(data,cursor,cursor+=2));
		katu = bytesToShort(Arrays.copyOfRange(data,cursor,cursor+=2));
		miss = bytesToShort(Arrays.copyOfRange(data,cursor,cursor+=2));
		
		score = bytesToInt(Arrays.copyOfRange(data,cursor,cursor+=4));
		combo = bytesToShort(Arrays.copyOfRange(data,cursor,cursor+=2));
		
		perfect = (data[cursor++]==0)? false : true;
		mods = bytesToInt(Arrays.copyOfRange(data,cursor,cursor+=4));
		
		cursor++;
		lengthtmp= data[cursor++];
		if(lengthtmp>0) lifebar = bytesToString(Arrays.copyOfRange(data,cursor,cursor+=lengthtmp));
		
		ticks = bytesToLong(Arrays.copyOfRange(data,cursor,cursor+=8));
		
		dataLength = bytesToInt(Arrays.copyOfRange(data,cursor,cursor+=4));
		InputStream lzmaStream = new ByteArrayInputStream(Arrays.copyOfRange(data,cursor,cursor+=dataLength));
		
		id = bytesToLong(Arrays.copyOfRange(data,cursor,cursor+=8));
		
		//unpack lzma
		try {
			OutputStream output = new OutputStream() {
				private StringBuilder string = new StringBuilder();
		        @Override
		        public void write(int b){
		            this.string.append((char) b );
		        }
	
		        //Netbeans IDE automatically overrides this toString()
		        public String toString(){
		            return this.string.toString();
		        }
			};
			int propertiesSize = 5;
			byte[] properties = new byte[propertiesSize];
			lzmaStream.read(properties, 0, propertiesSize);
			Decoder decoder = new Decoder();
			decoder.SetDecoderProperties(properties);
			long outSize = 0;
			for (int i = 0; i < 8; i++)
			{
				int v = lzmaStream.read();
				if (v < 0) System.out.println("cant read stream");
				outSize |= ((long)v) << (8 * i);
			}
			decoder.Code(lzmaStream, output, outSize);
			
			//parse frames from string
			frames = new ArrayList();
			String[] replayData = output.toString().split(",");
			for(int i=0; i<replayData.length; i++) {
				try {
					String d = replayData[i];
					String[] raw = d.split("\\|");
					frames.add(new ReplayFrame(Integer.parseInt(raw[0]),
							Float.parseFloat(raw[1]),
							Float.parseFloat(raw[2]),
							Integer.parseInt(raw[3])));
				}catch(Exception e) {
					continue;
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}	
	}
	
	
	byte[] readBytes(String path) {
		try {
			 File file = new File(path);
			 byte[] bytes = new byte[(int) file.length()]; 
			 FileInputStream fis = new FileInputStream(file);
			 fis.read(bytes);
			 fis.close();
			 return bytes;
		}catch(Exception e) {
			return null;
		}
	}
	
	public List<ReplayFrame> getFrames() {
		return frames;
	}
	
}