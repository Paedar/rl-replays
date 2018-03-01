package rocketleague.replays.parser.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.BitSet;

import rocketleague.replays.parser.RawData;

public class BitBuffer {
	private static final int FLOAT_LENGTH_BYTES = 4;
	private static final int DOUBLE_LENGTH_BYTES = 8;
	private static final int BYTE = 8;
	private final BitSet bits;
	private int position = 0;

	private BitBuffer(final BitSet bs) {
		bits = bs;
	}

	public static BitBuffer of(final BitSet bs) {
		if(bs == null) {
			throw new IllegalArgumentException("Data is a null reference");
		}
		return new BitBuffer(bs);
	}

	public static BitBuffer of(final byte[] data) {
		if(data == null) {
			throw new IllegalArgumentException("Data is a null reference");
		}
		return of(BitSet.valueOf(data));
	}

	public static BitBuffer of(final RawData data) {
		if(data == null) {
			throw new IllegalArgumentException("Data is a null reference");
		}
		return of(data.getRawBytes());
	}

	public int getPosition() {
		return position;
	}

	public int length() {
		return bits.length();
	}

	public boolean endOfStream() {
		return !hasMoreBits(1);
	}

	public boolean hasMoreBits(int numBits) {
		return position + numBits <= bits.size();
	}

	private void requireMoreBits(int numBits) {
		if(!hasMoreBits(numBits)) {
			throw new RuntimeException("Not enough bits left to read.");
		}
	}

	public boolean readBit() {
		requireMoreBits(1);
		boolean bit = bits.get(position);
		++position;
		return bit;
	}

	public int readBitAsInt() {
		return readBit() ? 1 : 0;
	}

	public byte readByte() {
		return readBytes(1)[0];
	}

	public byte[] readBytes(int numBytes) {
		if(numBytes < 0) {
			throw new IllegalArgumentException("Can't read a negative amount of bytes.");
		}
		int requiredBits = BYTE * numBytes;
		requireMoreBits(requiredBits);
		byte[] bytes = bits.get(position, position + requiredBits).toByteArray();
		position += requiredBits;
		return bytes;
	}

	public int readByteAsInt() {
		return 0xFF & readByte();
	}

	public int[] readBytesAsInt(int numBytes) {
		int[] ints = new int[numBytes];
		byte[] bytes = readBytes(numBytes);

		for(int i = 0; i < numBytes; ++i) {
			ints[i] = 0xFF & bytes[i];
		}
		return ints;
	}

	public float readFloat() {
		byte[] floatBytes = readBytes(FLOAT_LENGTH_BYTES);
		return ByteBuffer.wrap(floatBytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
	}

	public double readDouble() {
		byte[] doubleBytes = readBytes(DOUBLE_LENGTH_BYTES);
		return ByteBuffer.wrap(doubleBytes).order(ByteOrder.LITTLE_ENDIAN).getDouble();
	}

}
