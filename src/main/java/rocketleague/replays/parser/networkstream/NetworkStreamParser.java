package rocketleague.replays.parser.networkstream;

import java.util.List;
import java.util.stream.Collectors;

import rocketleague.replays.parser.metadata.Header;
import rocketleague.replays.parser.metadata.KeyFrame;
import rocketleague.replays.parser.util.BitBuffer;
import rocketleague.replays.parser.util.RawData;

public class NetworkStreamParser {
	private final RawData streamBytes;
	private final BitBuffer buffer;
	private final Header header;
	
	public NetworkStreamParser(RawData networkStream, Header header) {
		this.streamBytes = networkStream;
		this.buffer = BitBuffer.of(streamBytes.getRawBytes());
		this.header = header;
	}
	
	public List<Frame> parseKeyFrames(List<KeyFrame> keyFrames) {
		System.out.println("Max channels: " + header.maxChannels());
		List<Frame> frames = keyFrames.stream()
				.map((kf) -> { 
					buffer.setPosition(kf.filePosition);
					return Frame.from(buffer, this.header);
				})
				.collect(Collectors.toList());
		frames.forEach(System.out::println);
		return frames;
	}
}
