package rocketleague.replays.parser.util;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.BitSet;
import java.util.stream.IntStream;

public class BitBuffer {
	private static final int FLOAT_LENGTH_BYTES = 4;
	private static final int DOUBLE_LENGTH_BYTES = 8;
	private static final int INT_LENGTH_BYTES = 4;
	private static final int BYTE = 8;
	private final BitSet bits;
	private final int numberOfBits;
	private int position = 0;

	private BitBuffer(final BitSet bs) {
		bits = bs;
		numberOfBits = bs.length();
	}

	private BitBuffer(final byte[] bytes) {
		bits = BitSet.valueOf(bytes);
		numberOfBits = BYTE * bytes.length;
	}

	public static BitBuffer of(final BitSet bs) {
		if(bs == null) {
			throw new IllegalArgumentException("Data is a null reference");
		}
		return new BitBuffer(bs);
	}

	public static BitBuffer of(final byte[] bytes) {
		if(bytes == null) {
			throw new IllegalArgumentException("Data is a null reference");
		}
		return new BitBuffer(bytes);
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

	public void setPosition(int newPosition) {
		if(newPosition < 0 || newPosition > this.length()) {
			throw new IllegalArgumentException("Position out of bounds of the buffer.");
		}
		this.position = newPosition;
	}

	public int length() {
		return numberOfBits;
	}

	public boolean endOfStream() {
		return !hasMoreBits(1);
	}

	public boolean hasMoreBits(int numBits) {
		return position + numBits <= this.length();
	}

	private void requireMoreBits(int numBits) {
		if(!hasMoreBits(numBits)) {
			throw new BufferOverflowException();
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
		byte[] bytes = Arrays.copyOf(bits.get(position, position + requiredBits).toByteArray(), numBytes); // Ensures proper length, because the BitSet is weird
		position += requiredBits;
		return bytes;
	}

	private int byteAsInt(byte byteVal) {
		return 0xFF & byteVal;
	}

	public int readByteAsInt() {
		return byteAsInt(readByte());
	}

	public int[] readBytesAsInts(int numBytes) {
		int[] ints = new int[numBytes];
		byte[] bytes = readBytes(numBytes);

		for(int i = 0; i < numBytes; ++i) {
			ints[i] = byteAsInt(bytes[i]);
		}
		return ints;
	}

	public float readFloat() {
		int intBytes = readInt(FLOAT_LENGTH_BYTES * BYTE);
		return Float.intBitsToFloat(intBytes);
	}

	public double readDouble() {
		byte[] doubleBytes = readBytes(DOUBLE_LENGTH_BYTES);
		return ByteBuffer.wrap(doubleBytes).order(ByteOrder.LITTLE_ENDIAN).getDouble();
	}

	public int readInt(int numBits) {
		if(numBits < 0) {
			throw new IllegalArgumentException("Can't read a negative amount of bits");
		}
		if(numBits > BYTE * INT_LENGTH_BYTES) {
			throw new IllegalArgumentException("Integer max length is " + BYTE * INT_LENGTH_BYTES + " bits.");
		}
		requireMoreBits(numBits);
		
		return IntStream.range(0, numBits).sequential()
				.map((shift) -> readBitAsInt() << shift)
				.reduce(0, (acc, shiftedBit) -> acc | shiftedBit);
	}

}
