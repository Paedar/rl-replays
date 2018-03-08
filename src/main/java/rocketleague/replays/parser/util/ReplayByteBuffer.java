package rocketleague.replays.parser.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import org.apache.commons.codec.binary.StringUtils;

public class ReplayByteBuffer {

	private final ByteBuffer buffer;
	
	public ReplayByteBuffer(ByteBuffer delegate) {
		this.buffer = delegate;
	}
	
	public byte get() {
		return buffer.get();
	}

	public ByteBuffer get(byte[] dst) {
		return buffer.get(dst);
	}

	public double getDouble() {
		return buffer.getDouble();
	}

	public float getFloat() {
		return buffer.getFloat();
	}

	public short getShort() {
		return buffer.getShort();
	}

	public int getInt() {
		return buffer.getInt();
	}

	public long getLong() {
		return buffer.getLong();
	}

	public final boolean hasRemaining() {
		return buffer.hasRemaining();
	}

	public final int limit() {
		return buffer.limit();
	}

	public final ByteBuffer order(ByteOrder bo) {
		return buffer.order(bo);
	}

	public final int position() {
		return buffer.position();
	}

	public final int remaining() {
		return buffer.remaining();
	}
	
	public String readString() {
		int length = buffer.getInt();
		return readString(length);
	}

	public String readString(int numCharacters) {
		return readStringUtf8(numCharacters);
	}
	
	public String readStringUtf8(int numCharacters) {
		byte[] data = readBytes(numCharacters);
		return StringUtils.newStringUtf8(Arrays.copyOf(data, numCharacters - 1));
	}

	public String readStringUtf16(int numCharacters) {
		byte[] data = readBytes(numCharacters * 2);
		return StringUtils.newStringUtf16Le(Arrays.copyOf(data, 2 * (numCharacters - 1)));
	}
	
	public byte[] readBytes(int length) {
		byte[] data = new byte[length];
		buffer.get(data);
		return data;
	}
	
}
