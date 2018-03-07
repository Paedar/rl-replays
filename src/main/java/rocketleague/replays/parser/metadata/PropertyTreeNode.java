package rocketleague.replays.parser.metadata;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PropertyTreeNode {
	public final int classId;
	public String className;
	public final int parentId;
	public final int id;
	public final Map<Integer, Integer> properties;
	public final Map<Integer, String> classedProperties;

	public PropertyTreeNode(int classId, int parentId, int id) {
		this.classId = classId;
		this.parentId = parentId;
		this.id = id;
		this.properties = new HashMap<>();
		this.classedProperties = new HashMap<>();
	}

	@Override
	public String toString() {
		return "PropertyTreeNode [classId=" + classId + ", className=" + className
				+ ", id=" + id + ", parentId=" + parentId
				+ ", properties=" + properties
				+ "\nclassedProperties=\n\t" + classedProperties.entrySet().stream().map(Map.Entry::toString).collect(Collectors.joining("\n\t")) + "]";
	}
}
