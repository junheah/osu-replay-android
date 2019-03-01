package ml.junheah.osuparser.beatmap;

public class HitObject {
	long time;
	int x;
	int y;
	int type;
	public HitObject(long time, int x, int y, int type) {
		this.time = time;
		this.x = x;
		this.y = y;
		this.type = type;
	}
	
	public long getTime() {
		return time;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public boolean equals(Object arg0) {
	    return this.time == ((HitObject)arg0).getTime();
	}
}
