package rocketleague.replays.parser.metadata;

import rocketleague.replays.parser.util.ReplayBuffer;

public class DebugString {
	private final int frameNumber;
	public final String playerName;
	public final String debugString;

	public DebugString(int frameNumber, String playerName, String debugString) {
		this.frameNumber = frameNumber;
		this.playerName = playerName;
		this.debugString = debugString;
	}

	public static DebugString from(ReplayBuffer buffer) {
		return new DebugString(buffer.getInt(), buffer.readString(), buffer.readString());
	}

	@Override
	public String toString() {
		return "DebugString [frameNumber=" + frameNumber
				+ ", playerName=" + playerName
				+ ", debugString=" + debugString + "]";
	}
}
