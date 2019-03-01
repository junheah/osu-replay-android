package ml.junheah.osuparser.replay;


public class ReplayFrame {
	int timeDiff;
	float x,y;
	int index, input;
	public ReplayFrame(int timeDiff, float x, float y, int input) {
		this.timeDiff = timeDiff;
		this.x = x;
		this.y = y;
		this.input = input;
	}

	public float getX() {return x;}
	public float getY() {return y;}
	public int getTimeDiff() {return timeDiff;}
	public int getInput() {return input;}
	
}
