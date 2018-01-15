package rocketleague.replays.parser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public class FileLoader {

	public static RawData getFileData(String replayFile) {
		byte[] data = null;
		try {
			data = Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(replayFile).toURI()));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidPathException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return RawData.createFrom(data);
	}

}
