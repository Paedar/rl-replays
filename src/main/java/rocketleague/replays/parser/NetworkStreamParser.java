package rocketleague.replays.parser;

import java.util.BitSet;

public class NetworkStreamParser {
	private final RawData streamBytes;
	private final BitSet bits;
	
	public NetworkStreamParser(RawData networkStream) {
		this.streamBytes = networkStream;
		this.bits = BitSet.valueOf(streamBytes.getRawBytes());
	}
}
