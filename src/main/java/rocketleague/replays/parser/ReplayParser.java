package rocketleague.replays.parser;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class ReplayParser {

	private ByteBuffer buffer;
	
	public ReplayParser(RawData data) {
		this.buffer = ByteBuffer.wrap(data.getRawBytes());
	}
	
	public void parse() {
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		int propertiesLength = buffer.getInt();
		System.out.format("Properties length: %d\n", propertiesLength);
		byte[] crc = new byte[4];
		buffer.get(crc);
		System.out.format("CRC: %s.%s.%s.%s\n", 
				Byte.toString(crc[0]),
				Byte.toString(crc[1]),
				Byte.toString(crc[2]),
				Byte.toString(crc[3]));
		String versionNumber = String.format("%d.%d", buffer.getInt(), buffer.getInt());
		System.out.println(versionNumber);
		String version = readString();
		System.out.format("Version string: %s\n", version);
	}
	
	private String readString() {
		int length = buffer.getInt();
		byte[] data = new byte[length];
		buffer.get(data);
		String stringRead = new String(Arrays.copyOf(data, length - 1));
		return stringRead;
	}
}
