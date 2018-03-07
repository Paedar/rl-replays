package rocketleague.replays.parser;

import org.junit.Test;

import rocketleague.replays.parser.util.FileLoader;

public class ReplayParserTest {
	@Test
	public void testReader() {
		String replayFile = "1.11.replay";
//		String replayFile = "2s.replay";
//		String replayFile = "A28DA8C44B925B14DBD73592885A9507.replay";
		ReplayParser parser = new ReplayParser(FileLoader.getFileData(replayFile));

		parser.parse();
	}
}
