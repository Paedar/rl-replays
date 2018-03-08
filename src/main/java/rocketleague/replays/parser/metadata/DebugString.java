package rocketleague.replays.parser.metadata;

public class DebugString {
	private final int frameNumber;
	public final String playerName;
	public final String debugString;
	
	public DebugString(int frameNumber, String playerName, String debugString) {
		this.frameNumber = frameNumber;
		this.playerName = playerName;
		this.debugString = debugString;
	}
	
	public static DebugString of(int frameNumber, String playerName, String debugString) {
		return new DebugString(frameNumber, playerName, debugString);
	}

	@Override
	public String toString() {
		return "DebugString [playerName=" + playerName + ", debugString=" + debugString + "]";
	}
}
