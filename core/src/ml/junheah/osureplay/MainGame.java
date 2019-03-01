package ml.junheah.osureplay;

import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ml.junheah.osuparser.beatmap.Beatmap;
import ml.junheah.osuparser.beatmap.HitObject;
import ml.junheah.osuparser.replay.Replay;
import ml.junheah.osuparser.replay.ReplayFrame;

import com.badlogic.gdx.utils.TimeUtils;

public class MainGame extends ApplicationAdapter {
	private long lastTime=0;
	private long time;
	long currentTime;
	List<ReplayFrame> replayFrames;
	List<HitObject> hitObjects;
	int frameIndex = 0, objectIndex = 0, deltaStack, musicDelay, nextTimeDiff=0, visibleTime = 500, trailLength = 10;
	float circleSize = 25f, cursorSize = 10f;
	Music audio;
	Boolean playing = false;
	
	SpriteBatch batch;
	ShapeRenderer objectRenderer, cursorRenderer;
	BitmapFont font;

	Thread player;
	String song = "ayase";
	float posM;
	int plHeight, plWidth;
	
	@Override
	public void create () {
		Replay replay = new Replay(Gdx.files.internal(song+"/replay.osr").readBytes());
		replayFrames = replay.getFrames();
		for(;replayFrames.size()>frameIndex;frameIndex++) {
			int off = replayFrames.get(frameIndex).getTimeDiff();
			if(off>0) break;
			time+=off;
		}
		
		//beatmap
		Beatmap map = new Beatmap(Gdx.files.internal(song+"/map.osu").read());
		hitObjects = map.getHitObjects();
		
		//audio
		audio = Gdx.audio.newMusic(Gdx.files.internal(song+"/audio.mp3"));
		
		//font
		font = new BitmapFont();
		
		//play area specifications
		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();
		
		//playarea = 512x384
		plHeight = screenHeight-80;
		posM = (float)plHeight/384f;
		plWidth = Math.round(512f*posM);
		
		int playAreaXOff = (screenWidth-plWidth)/2;
		
		circleSize*=posM;
		cursorSize*=posM;
		
		//renderer
		objectRenderer = new ShapeRenderer();
		objectRenderer.scale(1f, -1f, 1f);
		objectRenderer.translate(playAreaXOff, -screenHeight+40, 0);
		
		cursorRenderer = new ShapeRenderer();
		cursorRenderer.scale(1f, -1f, 1f);
		cursorRenderer.translate(playAreaXOff, -screenHeight+40, 0);
		
		batch = new SpriteBatch();
		
		currentTime = TimeUtils.millis();


		player = new Thread(new Runnable() {
			@Override
			public void run() {
				audio.play();
			}
		});
	}
	
	public float getPos(float input) {
		return input*posM;
	}

	@Override
	public void render () {
		lastTime = currentTime;
		currentTime = TimeUtils.millis();
		update(currentTime-lastTime);
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//draw Objects
		objectRenderer.begin(ShapeType.Line);
		objectRenderer.rect(0,0,plWidth,plHeight);
		for(int i=objectIndex; i<hitObjects.size() && hitObjects.get(i).getTime()<time+visibleTime; i++) {
			HitObject object = hitObjects.get(i);
			objectRenderer.circle(getPos(object.getX()), getPos(object.getY()), circleSize);
		}
		objectRenderer.end();
		
		//draw replay
		cursorRenderer.begin(ShapeType.Filled);
		for(int i=frameIndex; i>=0 && i>frameIndex-trailLength; i--) {
			ReplayFrame frame = replayFrames.get(i);
			float size = ((float)((i-frameIndex)+trailLength))*((float)cursorSize/(float)trailLength);
			cursorRenderer.circle(getPos(frame.getX()), getPos(frame.getY()), size);
		}
		cursorRenderer.end();		
		
		batch.begin();
		//text
		font.setColor(255.0f, 255.0f, 255.0f, 255.0f);
		font.draw(batch, String.valueOf(time), 50, 50);
		
		batch.end();
	}
	
	
	@Override
	public void dispose () {
		batch.dispose();
		objectRenderer.dispose();
		cursorRenderer.dispose();
		audio.dispose();
		font.dispose();
	}
	
	public void update(Long delta) {
		if(delta>0) {
			//System.out.println(delta);
			nextTimeDiff-=delta;
			time+=delta;
			if(time>0 && !playing) {
				System.out.println(time);
				player.run();
				playing = true;
			}
			ReplayFrame frame = replayFrames.get(frameIndex);
			ReplayFrame nextFrame = replayFrames.get(frameIndex+1);
			//check if nextframe.timediff > delta
			while(nextTimeDiff<=0 && frameIndex<replayFrames.size()-2) {
				frameIndex++;
				frame = replayFrames.get(frameIndex);
				nextFrame = replayFrames.get(frameIndex+1);
				nextTimeDiff+=nextFrame.getTimeDiff();
			}
			for(;objectIndex<hitObjects.size() && time>hitObjects.get(objectIndex).getTime();objectIndex++) {
				
			}
		}
	}
}
