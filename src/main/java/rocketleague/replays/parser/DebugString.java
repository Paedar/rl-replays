package rocketleague.replays.parser;

public class DebugString {
	public final String playerName;
	public final String debugString;
	
	public DebugString(String playerName, String debugString) {
		this.playerName = playerName;
		this.debugString = debugString;
	}
	
	public static DebugString of(String playerName, String debugString) {
		return new DebugString(playerName, debugString);
	}

	@Override
	public String toString() {
		return "DebugString [playerName=" + playerName + ", debugString=" + debugString + "]";
	}
}
