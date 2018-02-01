package rocketleague.replays.parser;

import java.util.HashMap;
import java.util.Map;

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
		return "PropertyTreeNode [classId=" + classId + ", className=" + className + ", parentId=" + parentId + ", id="
				+ id + ", properties=" + properties + ", classedProperties=" + classedProperties + "]";
	}
}
