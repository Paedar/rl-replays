package rocketleague.replays.parser.networkstream;

import rocketleague.replays.parser.metadata.Header;
import rocketleague.replays.parser.util.BitBuffer;

public class Frame {
	private int streamPosition;
	private float time;
	private float timeDelta;

	private Frame() {
		// NOOP
	}

	public static Frame from(BitBuffer buffer, Header properties) {
		if(buffer == null || properties == null) {
			throw new IllegalArgumentException("Buffer and properties can't be null");
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
