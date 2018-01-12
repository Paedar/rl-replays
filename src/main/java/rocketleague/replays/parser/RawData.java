package rocketleague.replays.parser;

import java.util.Arrays;

public class RawData {
	private byte[] rawBytes;

	private RawData(byte[] data) {
		rawBytes = data;
	}
	
	public static RawData createFrom(byte[] data) {
		if(data == null) {
			throw new IllegalArgumentException("Data is a null reference");
		}
		return new RawData(data);
	}

	public byte[] getRawBytes() {
		return Arrays.copyOf(rawBytes, rawBytes.length);
	}
	
	public byte[] getRawBytes(int from, int to) {
		return Arrays.copyOfRange(rawBytes, from, to);
	}

	public int length() {
		return rawBytes.length;
	}
}
