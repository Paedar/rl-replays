package rocketleague.replays.parser.networkstream;

import rocketleague.replays.parser.util.ReplayBuffer;

public class ClassNetCacheProperty {
	private final int index;
	private final int id;

	private ClassNetCacheProperty(int index, int id) {
		this.index = index;
		this.id = id;
	}

	public static ClassNetCacheProperty from(ReplayBuffer buffer) {
		if(buffer == null) {
			throw new IllegalArgumentException("Buffer can't be null.");
		}
		return new ClassNetCacheProperty(buffer.getInt(), buffer.getInt());
	}

	public int getIndex() {
		return index;
	}

	public int getId() {
		return id;
	}
}
