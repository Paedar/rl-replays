package rocketleague.replays.parser.metadata;

public class HeaderProperty<T> {
	public final String key;
	public final T value;
	public final Class<T> type;

	public HeaderProperty(String key, T value, Class<T> clazz) {
		this.key = key;
		this.value = value;
		this.type = clazz;
	}
	
	@Override
	public String toString() {
		return String.format("[%s, %s, %s]", key, type.getSimpleName(), type.cast(value).toString());
	}
}
