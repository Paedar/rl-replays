package rocketleague.replays.parser.networkstream;

import rocketleague.replays.parser.util.BitBuffer;

public class Frame {
	private int streamPosition;
	private float time;
	private float timeDelta;

	private Frame() {
		// NOOP
	}

	public static Frame from(BitBuffer buffer) {
		if(buffer == null) {
			throw new IllegalArgumentException("Buffer can't be null");
		}
		Frame frame = new Frame();
		frame.streamPosition = buffer.getPosition();
		frame.time = buffer.readFloat();
		frame.timeDelta = buffer.readFloat();
		return frame;
	}

	@Override
	public String toString() {
		return "Frame [streamPosition=" + streamPosition + ",\n"
				+ "time=" + time + ",\n"
				+ "timeDelta=" + timeDelta + "]";
	}

}
