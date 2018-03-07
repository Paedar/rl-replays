package rocketleague.replays.parser.metadata;

import java.util.Arrays;
import java.util.List;

public class Header {
	private final int headerLength;
	private final byte[] crc;
	private final ReplayVersion version;
	private final String versionString; // Apparently this is currently always TAGame.Replay_Soccar_TA
	private final List<HeaderProperty<?>> headerProperties;
	
	public Header(int headerLength, byte[] crc, ReplayVersion version, String versionString,
			List<HeaderProperty<?>> headerProperties) {
		this.headerLength = headerLength;
		this.crc = crc;
		this.version = version;
		this.versionString = versionString;
		this.headerProperties = headerProperties;
	}

	@Override
	public String toString() {
		return "Header [\n"
				+ "\t headerLength=" + headerLength + ",\n"
				+ "\t crc=" + Arrays.toString(crc) + ",\n"
				+ "\t version=" + version + ",\n"
				+ "\t versionString=" + versionString + ",\n"
				+ "\t headerProperties=" + headerProperties + "\n]";
	}
	
	public int maxChannels() {
		return headerProperties.stream()
				.filter((p) -> "MaxChannels".equals(p.key))
				.findFirst()
				.map(obj -> (Integer) obj.value)
				.orElseThrow(() -> new RuntimeException("MaxChannels property not found"));
	}
	

}
