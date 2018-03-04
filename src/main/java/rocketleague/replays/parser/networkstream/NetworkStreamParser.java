package rocketleague.replays.parser.networkstream;

import java.util.ArrayList;
import java.util.List;

import rocketleague.replays.parser.util.BitBuffer;
import rocketleague.replays.parser.util.RawData;

public class NetworkStreamParser {
	private final RawData streamBytes;
	private final BitBuffer buffer;
	
	public NetworkStreamParser(RawData networkStream) {
		this.streamBytes = networkStream;
		this.buffer = BitBuffer.of(streamBytes.getRawBytes());
	}
	
	public List<Frame> parse() {
		List<Frame> frames = new ArrayList<>(); 
		readFrame();

		return frames;
	}
	
	public Frame readFrame() {
		Frame frame = Frame.from(buffer);
		System.out.println(frame);
		return frame;
	}
}
