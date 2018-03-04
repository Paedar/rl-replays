package rocketleague.replays.parser.metadata;

public class ByteProperty {
	public final String byteKey;
	public final String byteValue;
	
	public ByteProperty(String byteKey, String byteValue) {
		this.byteKey = byteKey;
		this.byteValue = byteValue;
	}
	
	public static ByteProperty of(String byteKey, String byteValue) {
		return new ByteProperty(byteKey, byteValue);
	}
	
	public String toString() {
		return String.format("[%s: %s]", byteKey, byteValue);
	}
}
