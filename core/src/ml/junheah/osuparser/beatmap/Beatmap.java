package ml.junheah.osuparser.beatmap;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Beatmap {
	//TODO: cs, ar, sliders
	
	List<HitObject> objects;
	public Beatmap(InputStream stream){
		parse(stream);
		
	}
	
	void parse(InputStream stream) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
			String line = reader.readLine();
			while(line!=null) {
				if(line.contains("[General]")) {
					line = parseGeneral(reader);
					continue;
				}else if(line.contains("[Metadata]")) {
					line = parseMeta(reader);
					continue;
				}else if(line.contains("[Difficulty]")) {
					line = parseDifficulty(reader);
					continue;
				}else if(line.contains("[Events]")) {
					line = parseEvents(reader);
					continue;
				}else if(line.contains("[TimingPoints]")) {
					line = parseTimingPoints(reader);
					continue;
				}else if(line.contains("[Colours]")) {
					line = parseColours(reader);
					continue;
				}else if(line.contains("[HitObjects]")) {
					line = parseHitObjects(reader);
					continue;
				}
				line = reader.readLine();
			}
		}catch(Exception e) {e.printStackTrace();}
	}
	
	String parseGeneral(BufferedReader reader) {
		try {
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				if(line.contains("[")) return line;
				//parse general data
			}
		}catch(Exception e) {e.printStackTrace();}
		return "";
	}
	
	String parseMeta(BufferedReader reader) {
		try {
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				if(line.contains("[")) return line;
				//parse metadata
			}
		}catch(Exception e) {e.printStackTrace();}
		return "";
	}
	
	String parseEvents(BufferedReader reader) {
		try {
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				if(line.contains("[")) return line;
				//parse events
				
			}
		}catch(Exception e) {e.printStackTrace();}
		return "";
	}
	
	String parseDifficulty(BufferedReader reader) {
		try {
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				if(line.contains("[")) return line;
				//parse diff
				
			}
		}catch(Exception e) {e.printStackTrace();}
		return "";
	}
	
	String parseTimingPoints(BufferedReader reader) {
		try {
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				if(line.contains("[")) return line;
				//parse timingPoints
				
			}
		}catch(Exception e) {e.printStackTrace();}
		return "";
	}
	
	String parseColours(BufferedReader reader) {
		try {
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				if(line.contains("[")) return line;
				//parse colours
				
			}
		}catch(Exception e) {e.printStackTrace();}
		return "";
	}
	
	String parseHitObjects(BufferedReader reader) {
		objects = new ArrayList<HitObject>();
		try {
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				if(line.contains("[")) return line;
				//parse hit objects
				// x, y, time, type
				// only parses x y time
				String[] raw = line.split(",");
				objects.add(new HitObject(Long.parseLong(raw[2]),Integer.parseInt(raw[0]),Integer.parseInt(raw[1]),Integer.parseInt(raw[3])));
			}
		}catch(Exception e) {e.printStackTrace();}
		return "";
	}
	
	public List<HitObject> getHitObjects(){
		return objects;
	}
}
