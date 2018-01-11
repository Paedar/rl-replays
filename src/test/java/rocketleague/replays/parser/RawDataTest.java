package rocketleague.replays.parser;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class RawDataTest {

	@Test(expected = IllegalArgumentException.class)
	public void testCreatorWithNull() {
		RawData rawData = RawData.buildFrom(null);
	}
	
	@Test
	public void testCreatorWithData() {
		byte[] data = {0, 1, 2, 3};
		RawData rawData = RawData.buildFrom(data);
		
		assertNotNull(rawData);
		assertEquals(data.length, rawData.length());
		assertNotEquals(data, rawData.getRawBytes());
		assertArrayEquals(data, rawData.getRawBytes());
	}
	
	@Test
	public void testGetRawBytesRange() {
		byte[] data = {0, 1, 2};
		RawData rawData = RawData.buildFrom(data);

		byte[] shortestBit = {0};
		byte[] longerBit = {0, 1};
		byte[] endBit = {1, 2};
		assertArrayEquals(shortestBit, rawData.getRawBytes(0, 1));
		assertArrayEquals(longerBit, rawData.getRawBytes(0, 2));
		assertArrayEquals(endBit, rawData.getRawBytes(1, 3));
		assertArrayEquals(data, rawData.getRawBytes(0, 3));
	}
}
