package rocketleague.replays.parser.networkstream;

import rocketleague.replays.parser.RawData;
import rocketleague.replays.parser.util.BitBuffer;

public class NetworkStreamParser {
	private final RawData streamBytes;
	private final BitBuffer buffer;
	
	public NetworkStreamParser(RawData networkStream) {
		this.streamBytes = networkStream;
		this.buffer = BitBuffer.of(streamBytes.getRawBytes());
	}
	
	public void parse() {
		readFrame();
	}
	
	public void readFrame() {
		Frame frame = Frame.from(buffer);
		System.out.println(frame);
	}
}
