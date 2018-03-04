package rocketleague.replays.parser.util;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.BitSet;

import org.junit.Before;
import org.junit.Test;

public class BitBufferTest {

	private BitSet testBits;

	@Before
	public void setupBits() {
		byte[] bytes = { 0b00000001, (byte) 0b11010001};
		testBits = BitSet.valueOf(bytes);
	}

	@Test
	public void testLength() {
		BitBuffer buffer = BitBuffer.of(testBits);

		assertEquals(16, buffer.length());
	}

	@Test
	public void testHasMoreBits() {
		BitBuffer buffer = BitBuffer.of(testBits);
		assertTrue(buffer.hasMoreBits(15));
		assertTrue(buffer.hasMoreBits(16));
		assertFalse(buffer.hasMoreBits(17));
	}

	@Test
	public void testEndOfStream() {
		BitBuffer buffer = BitBuffer.of(testBits);
		assertFalse(buffer.endOfStream());

		buffer.readByte();
		assertFalse(buffer.endOfStream());

		buffer.readByte();
		assertTrue(buffer.endOfStream());
	}

	@Test
	public void testReadBit() {
		BitBuffer buffer = BitBuffer.of(testBits);
		assertTrue(buffer.readBit());
		assertFalse(buffer.readBit());
		assertFalse(buffer.readBit());
		assertFalse(buffer.readBit());

		assertEquals(4, buffer.getPosition());

		assertFalse(buffer.readBit());
		assertFalse(buffer.readBit());
		assertFalse(buffer.readBit());
		assertFalse(buffer.readBit());

		assertEquals(8, buffer.getPosition());

		assertTrue(buffer.readBit());
		assertFalse(buffer.readBit());
		assertFalse(buffer.readBit());
		assertFalse(buffer.readBit());
		assertTrue(buffer.readBit());
		assertFalse(buffer.readBit());
		assertTrue(buffer.readBit());
		assertTrue(buffer.readBit());
	}
	
	@Test
	public void testReadBitAsInt() {
		BitBuffer buffer = BitBuffer.of(testBits);
		
		assertEquals(1, buffer.readBitAsInt());
		assertEquals(0, buffer.readBitAsInt());
		assertEquals(0, buffer.readBitAsInt());
		assertEquals(0, buffer.readBitAsInt());
		assertEquals(4, buffer.getPosition());
	}

	@Test
	public void testReadByte() {
		BitBuffer buffer = BitBuffer.of(testBits);

		assertEquals(1, buffer.readByte());
		assertEquals(8, buffer.getPosition());
		assertEquals(-47, buffer.readByte());
	}
	
	@Test
	public void testReadBytes() {
		BitBuffer buffer = BitBuffer.of(testBits);
		byte[] bytes = buffer.readBytes(2);
		assertEquals(16, buffer.getPosition());
		assertEquals(2, bytes.length);
		assertEquals(1, bytes[0]);
		assertEquals(-47, bytes[1]);
	}

	@Test
	public void testReadByteAsInt() {
		BitBuffer buffer = BitBuffer.of(testBits);
		assertEquals(1, buffer.readByteAsInt());
		assertEquals(8, buffer.getPosition());
		assertEquals(209, buffer.readByteAsInt());
		assertEquals(16, buffer.getPosition());
	}
	
	@Test
	public void testReadBytesAsInt() {
		BitBuffer buffer = BitBuffer.of(testBits);
		int[] ints = buffer.readBytesAsInt(2);
		assertEquals(16, buffer.getPosition());
		assertEquals(2, ints.length);
		assertEquals(1, ints[0]);
		assertEquals(209, ints[1]);
	}
	
	@Test
	public void testReadFloat() {
		float floatPi = 3.14f;
		byte[] floatBytes = ByteBuffer.allocate(4)
				.order(ByteOrder.LITTLE_ENDIAN)
				.putFloat(floatPi)
				.array();
		BitBuffer floatBuffer = BitBuffer.of(floatBytes);
		
		assertEquals(32, floatBuffer.length());
		assertEquals(floatPi, floatBuffer.readFloat(), 0.0f);
		assertEquals(32, floatBuffer.getPosition());
	}
	
	@Test
	public void testReadDouble() {
		double doublePi = 3.14;
		byte[] doubleBytes = ByteBuffer.allocate(8)
				.order(ByteOrder.LITTLE_ENDIAN)
				.putDouble(doublePi)
				.array();
		BitBuffer doubleBuffer = BitBuffer.of(doubleBytes);

		assertEquals(64, doubleBuffer.length());
		assertEquals(doublePi, doubleBuffer.readDouble(), 0.0);
		assertEquals(64, doubleBuffer.getPosition());
	}
	
	@Test
	public void testReadIntFromBits() {
		BitBuffer buffer = BitBuffer.of(testBits);

		assertEquals(1, buffer.readInt(5));
	}

}
