package rocketleague.replays.parser;

import java.util.HashMap;
import java.util.Map;

public class PropertyTreeNode {
	public final int clazz;
	public final int parentId;
	public final int id;
	public final Map<Integer, Integer> properties;
	
	public PropertyTreeNode(int clazz, int parentId, int id) {
		this.clazz = clazz;
		this.parentId = parentId;
		this.id = id;
		this.properties = new HashMap<>();
	}

	@Override
	public String toString() {
		return "PropertyTreeNode [clazz=" + clazz + ", parentId=" + parentId + ", id=" + id + ", properties="
				+ properties + "]";
	}
}
