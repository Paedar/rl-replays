package rocketleague.replays.parser.metadata;

import rocketleague.replays.parser.util.ReplayBuffer;

public class GoalTick {
	public final String team;
	public final int frame;
	
	public GoalTick(String team, int frame) {
		this.team = team;
		this.frame = frame;
	}

	public static GoalTick from(ReplayBuffer buffer) {
		return new GoalTick(buffer.readString(), buffer.getInt());
	}
	
	@Override
	public String toString() {
		return "GoalTick [team=" + team + ", frame=" + frame + "]";
	}
}
