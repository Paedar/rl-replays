package rocketleague.replays.parser.metadata;

public class GoalTick {
	public final String team;
	public final int frame;
	
	public GoalTick(String team, int frame) {
		this.team = team;
		this.frame = frame;
	}
	
	public static GoalTick of(String team, int frame) {
		return new GoalTick(team, frame);
	}

	@Override
	public String toString() {
		return "GoalTick [team=" + team + ", frame=" + frame + "]";
	}
}
