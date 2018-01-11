package rocketleague.replays.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class ReplayParserTest {

	private ReplayParser parser;

	@Before
	public void createParser() {
		parser = new ReplayParser();
	}

	@Test
	public void testOpenFile() {
		RawData data;
		String filePath = "testfile_8bytes.data";

		data = parser.getFileData(filePath);

		assertNotNull(data);
		assertEquals(8, data.length());
	}

	@Test
	public void testOpenReplayFile() {
		RawData data;
		String filePath = "2s.replay";
		
		data = parser.getFileData(filePath);

		assertNotNull(data);
		assertEquals(14109, data.length());
		System.out.println(new String(data.getRawBytes(0, 1000)));
	}
}
