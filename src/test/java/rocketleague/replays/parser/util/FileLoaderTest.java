package rocketleague.replays.parser.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import rocketleague.replays.parser.util.FileLoader;
import rocketleague.replays.parser.util.RawData;

public class FileLoaderTest {

	@Test
	public void testOpenFile() {
		RawData data;
		String replayFile = "testfile_8bytes.data";

		data = FileLoader.getFileData(replayFile);

		assertNotNull(data);
		assertEquals(8, data.length());
	}

	@Test
	public void testOpenReplayFile() {
		RawData data;
		String replayFile = "2s.replay";

		data = FileLoader.getFileData(replayFile);

		assertNotNull(data);
		assertEquals(14109, data.length());
	}
}
