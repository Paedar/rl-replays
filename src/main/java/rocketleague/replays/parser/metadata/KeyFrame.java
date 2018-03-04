package rocketleague.replays.parser.metadata;
// return {
// 'time': time,
// 'frame': frame,
// 'file_position': file_position
// }
public class KeyFrame {
	public final float time;
	public final int frame;
	public final int filePosition;
	
	public KeyFrame(float time, int frame, int filePosition) {
		this.time = time;
		this.frame = frame;
		this.filePosition = filePosition;
	}
	
	public static KeyFrame of(float time, int frame, int filePosition) {
		return new KeyFrame(time, frame, filePosition);
	}

	@Override
	public String toString() {
		return "KeyFrame [time=" + time + ", frame=" + frame + ", filePosition=" + filePosition + "]";
	}
}
