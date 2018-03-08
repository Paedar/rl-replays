package rocketleague.replays.parser.metadata;

import rocketleague.replays.parser.util.ReplayBuffer;

public class KeyFrame {
	public final float time;
	public final int frame;
	public final int filePosition;

	public KeyFrame(float time, int frame, int filePosition) {
		this.time = time;
		this.frame = frame;
		this.filePosition = filePosition;
	}

	public static KeyFrame from(ReplayBuffer buffer) {
		return new KeyFrame(buffer.getFloat(), buffer.getInt(), buffer.getInt());
	}

	@Override
	public String toString() {
		return "KeyFrame [time=" + time + ", frame=" + frame + ", filePosition=" + filePosition + "]";
	}

}
