package rocketleague.replays.parser.networkstream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassNetCache {
	private final int objectIndex;
	private final int parentId;
	private final int id;
	private final Map<Integer, ClassNetCacheProperty> properties;
	private final List<ClassNetCache> children;
	private ClassNetCache parent;

	public ClassNetCache(int objectIndex, int parentId, int id, Map<Integer, ClassNetCacheProperty> properties) {
		super();
		this.objectIndex = objectIndex;
		this.parentId = parentId;
		this.id = id;
		this.properties = properties;
		this.children = new ArrayList<>();
	}

	public static Builder getBuilder() {
		return new Builder();
	}

	public static class Builder {
		private int builderObjectIndex;
		private int builderParentId;
		private int builderId;
		private Map<Integer, ClassNetCacheProperty> builderProperties;

		private Builder() {
			// NOOP
		}

		public ClassNetCache build() {
			return new ClassNetCache(builderObjectIndex,
					builderParentId,
					builderId,
					builderProperties);
		}

		public Builder objectIndex(int objectIndex) {
			this.builderObjectIndex = objectIndex;
			return this;
		}

		public Builder parentId(int parentId) {
			this.builderParentId = parentId;
			return this;
		}

		public Builder id(int id) {
			this.builderId = id;
			return this;
		}

		public Builder properties(Map<Integer, ClassNetCacheProperty> properties) {
			this.builderProperties = properties;
			return this;
		}
	}

	public int getId() {
		return id;
	}

	public int getParentId() {
		return parentId;
	}
	
	public void setParent(ClassNetCache parent) {
		this.parent = parent;
	}
	
	public void addChild(ClassNetCache child) {
		this.children.add(child);
	}
}
