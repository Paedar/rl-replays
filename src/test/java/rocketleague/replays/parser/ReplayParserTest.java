package rocketleague.replays.parser;

import org.junit.Test;

public class ReplayParserTest {
	@Test
	public void testReader() {
		String replayFile = "1.11.replay";
		ReplayParser parser = new ReplayParser(FileLoader.getFileData(replayFile));

		parser.parse();
	}
}
