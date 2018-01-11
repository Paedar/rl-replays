package rocketleague.replays.parser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public class ReplayParser {

	public RawData getFileData(String replayFile) {
		byte[] data = null;
		try {
			data = Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(replayFile).toURI()));
		} catch (IOException e) {
			System.out.println("Sorry, I'm not actually dealing with this shit.");
			e.printStackTrace();
		} catch (InvalidPathException e) {
			System.out.println("Sorry, I'm not actually dealing with this shit.");
			e.printStackTrace();
		} catch (URISyntaxException e) {
			System.out.println("Sorry, I'm not actually dealing with this shit.");
			e.printStackTrace();
		}
		return RawData.buildFrom(data);
	}

}
