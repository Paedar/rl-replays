package rocketleague.replays.parser;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import rocketleague.replays.parser.metadata.ByteProperty;
import rocketleague.replays.parser.metadata.DebugString;
import rocketleague.replays.parser.metadata.GoalTick;
import rocketleague.replays.parser.metadata.Header;
import rocketleague.replays.parser.metadata.HeaderProperty;
import rocketleague.replays.parser.metadata.KeyFrame;
import rocketleague.replays.parser.metadata.PropertyTreeNode;
import rocketleague.replays.parser.metadata.ReplayVersion;
import rocketleague.replays.parser.networkstream.NetworkStreamParser;
import rocketleague.replays.parser.util.RawData;
import rocketleague.replays.parser.util.ReplayBuffer;

public class ReplayParser {

	private ReplayBuffer buffer;
	private RawData rawData;

	public ReplayParser(RawData data) {
		this.buffer = new ReplayBuffer(ByteBuffer.wrap(data.getRawBytes()).order(ByteOrder.LITTLE_ENDIAN));
		this.rawData = data;
	}

	public Replay parse() {
		Replay.Builder replayBuilder = Replay.getBuilder();
		replayBuilder.rawData(this.rawData);

		System.out.println("Buffer limit: " + buffer.limit());

		// Start of header
		int propertiesLength = buffer.getInt();
		byte[] crc = new byte[4];
		buffer.get(crc);
		ReplayVersion replayVersion = ReplayVersion.readFrom(buffer);
		String versionString = buffer.readString();
		List<HeaderProperty<?>> headerProperties = readProperties();

		Header header = new Header(propertiesLength, crc, replayVersion, versionString, headerProperties);
		replayBuilder.header(header);
		System.out.println(header);
		// End of header
		System.out.println("Buffer position: " + buffer.position());

		int remainingLength = buffer.getInt();
		System.out.println("Remaining length of data: " + remainingLength);

		byte[] crc2 = new byte[4];
		buffer.get(crc2);
		System.out.format("CRC2: %s.%s.%s.%s\n", Byte.toString(crc2[0]), Byte.toString(crc2[1]), Byte.toString(crc2[2]),
				Byte.toString(crc2[3]));

		List<String> levelInfo = readLevelInfo();
		System.out.println(levelInfo);
		System.out.println("Buffer position: " + buffer.position());

		List<KeyFrame> keyFrames = readKeyFrames();
		System.out.println("-- KeyFrames --");
		System.out.println(keyFrames);
		System.out.println("Buffer position: " + buffer.position());

		RawData networkStream = readNetworkStream();
		System.out.println("Network stream length: " + networkStream.length());
		System.out.println("Buffer position: " + buffer.position());

		List<DebugString> debugStrings = readDebugStrings();
		System.out.println("-- Debug Strings --");
		System.out.println(debugStrings);
		System.out.println("Buffer position: " + buffer.position());

		List<GoalTick> goalTicks = readGoalTicks();
		System.out.println("-- Goal ticks --");
		System.out.println(goalTicks);
		System.out.println("Buffer position: " + buffer.position());

		List<String> packages = readPackages();
		System.out.println("-- Packages --");
		System.out.println(packages);
		System.out.println("Buffer position: " + buffer.position());

		// data['objects'] = self._read_objects(replay_file)
		List<String> objects = readObjects();
		System.out.println("-- Objects --" );
//		System.out.println(objects);
		objects.stream().forEach(System.out::println);
		System.out.println("Buffer position: " + buffer.position());

		// data['name_table'] = self._read_name_table(replay_file)
		List<String> nameTable = readNameTable();
		System.out.println("-- Nametable --");
//		System.out.println(nameTable);
		nameTable.stream().forEach(System.out::println);
		System.out.println("Buffer position: " + buffer.position());

		// data['classes'] = self._read_classes(replay_file)
		Map<Integer, String> classes = readClasses();
		System.out.println("-- Classes --");
		System.out.println(classes);
		System.out.println("Buffer position: " + buffer.position());

		// data['property_tree'] = self._read_property_tree(replay_file,
		// data['objects'], data['classes'])
		List<PropertyTreeNode> propertyTree = readPropertyTree(objects, classes);
		System.out.println("-- Property Tree -- To be renamed...");
//		System.out.println(propertyTree);
		propertyTree.stream().forEach(System.out::println);
		System.out.println("Buffer position: " + buffer.position());

		// assert replay_file.tell() == properties_length + remaining_length + 16
		System.out.println("Expected position: " + (propertiesLength + remainingLength + 16));

		// # Run some manual parsing operations.
		// data = self.manual_parse(data, replay_file)

		System.out.println(" --- Parsing Network Stream ---");
		NetworkStreamParser nsp = new NetworkStreamParser(networkStream, header);
		nsp.parseKeyFrames(keyFrames);

		return replayBuilder.build();
	}

	private List<String> readLevelInfo() {
		return buffer.readStringList();
	}

	private List<KeyFrame> readKeyFrames() {
		return Stream.generate(() -> KeyFrame.from(buffer))
				.limit(buffer.getInt())
				.collect(Collectors.toList());
	}

	private RawData readNetworkStream() {
		int streamLength = buffer.getInt();
		return RawData.createFrom(buffer.readBytes(streamLength));
	}

	private List<DebugString> readDebugStrings() {
		return Stream.generate(() -> DebugString.from(buffer))
				.limit(buffer.getInt())
				.collect(Collectors.toList());
	}

	private List<GoalTick> readGoalTicks() {
		int numberOfGoals = buffer.getInt();
		System.out.println("Number of goals: " + numberOfGoals);
		return Stream.generate(() -> GoalTick.from(buffer))
				.limit(numberOfGoals)
				.collect(Collectors.toList());
	}

	private List<String> readPackages() {
		return buffer.readStringList();
	}

	// def _read_objects(self, replay_file):
	// num_objects = self._read_integer(replay_file)
	//
	// objects = []
	//
	// for x in xrange(num_objects):
	// objects.append(self._read_string(replay_file))
	//
	// return objects
	private List<String> readObjects() {
		return buffer.readStringList();
	}

	// def _read_name_table(self, replay_file):
	// name_table_length = self._read_integer(replay_file)
	// table = []
	//
	// for x in xrange(name_table_length):
	// table.append(self._read_string(replay_file))
	//
	// return table
	private List<String> readNameTable() {
		return buffer.readStringList();
	}

	// def _read_classes(self, replay_file):
	// class_index_map_length = self._read_integer(replay_file)
	//
	// class_index_map = {}
	//
	// for x in xrange(class_index_map_length):
	// name = self._read_string(replay_file)
	// integer = self._read_integer(replay_file)
	//
	// class_index_map[integer] = name
	//
	// return class_index_map
	private Map<Integer, String> readClasses() {
		Map<Integer, String> classes = new HashMap<>();
		int numberOfClasses = buffer.getInt();
		IntStream.range(0, numberOfClasses)
				.forEach(i -> {
					String name = buffer.readString();
					int id = buffer.getInt();
					classes.put(id, name);
				});

		return classes;
	}

	private List<PropertyTreeNode> readPropertyTree(List<String> objects, Map<Integer, String> classes) {
		// def _read_property_tree(self, replay_file, objects, classes):
		// branches = []
		//
		// property_tree_length = self._read_integer(replay_file)
		//
		// for x in xrange(property_tree_length):
		// data = {
		// 'class': self._read_integer(replay_file),
		// 'parent_id': self._read_integer(replay_file),
		// 'id': self._read_integer(replay_file),
		// 'properties': {}
		// }
		//
		// if data['id'] == data['parent_id']:
		// data['id'] = 0
		//
		// length = self._read_integer(replay_file)
		//
		// for x in xrange(length):
		// index = self._read_integer(replay_file)
		// value = self._read_integer(replay_file)
		//
		// data['properties'][index] = value
		//
		// branches.append(data)

		int propertyTreeLength = buffer.getInt();
		List<PropertyTreeNode> propertyTree = new ArrayList<>(propertyTreeLength);
		for(int i = 0; i < propertyTreeLength; ++i) {
			int classId = buffer.getInt();
			int parentId = buffer.getInt();
			int id = buffer.getInt();
			if(parentId == id) {
				id = 0;
			}
			PropertyTreeNode node = new PropertyTreeNode(classId, parentId, id);
			int numberOfProperties = buffer.getInt();
			for(int j = 0; j < numberOfProperties; ++j) {
				node.properties.put(buffer.getInt(), buffer.getInt());
			}

			propertyTree.add(node);
		}

		// # Map the property keys against the class list.
		// classed = {}

		// for branch in branches:
		// # {'parent_id': 36, 'properties': {42: 36}, 'class': 43, 'id': 37}
		// classed[branch['class']] = {
		// 'class': classes[branch['class']],
		// 'properties': map_properties(branch['id'] if branch['id'] > 0 else
		// branch['parent_id'])
		// }
		//
		// return branches

		for(PropertyTreeNode ptn : propertyTree) {
			ptn.className = classes.get(ptn.classId);
			ptn.classedProperties.putAll(mapProperties(objects, propertyTree, ptn.id > 0 ? ptn.id : ptn.parentId));
		}

		return propertyTree;
	}

	private Map<Integer, String> mapProperties(List<String> objects, List<PropertyTreeNode> propertyTree, int id) {
		// def map_properties(id):
		// for branch in branches:
		// if branch['id'] == id:
		// props = {}
		//
		// if branch['parent_id'] > 0:
		// props = map_properties(branch['parent_id'])
		//
		// for k, v in enumerate(branch['properties']):
		// props[v] = objects[k]
		//
		// return props
		//
		// return {}

		for(PropertyTreeNode ptn : propertyTree) {
			if(ptn.id == id) {
				Map<Integer, String> props;
				if(ptn.parentId > 0) {
					props = mapProperties(objects, propertyTree, ptn.parentId);
				} else {
					props = new HashMap<>();
				}
				props.putAll(
						ptn.properties.entrySet().stream()
								.collect(Collectors.toMap(
//										 Map.Entry::getKey,
//										 e -> objects.get(e.getValue()))));
										// Note that it looks like key and value get swapped here, but so does the
										// python lib
										Map.Entry::getValue,
										e -> objects.get(e.getKey()))));
				return props;
			}
		}

		return new HashMap<>();
	}

	private List<HeaderProperty<?>> readProperties() {
		List<HeaderProperty<?>> properties = new ArrayList<>();
		HeaderProperty<?> property;
		do {
			property = readProperty();
			if(property != null) {
				properties.add(property);
			}
		} while(property != null);
		return properties;
	}

	private HeaderProperty<?> readProperty() {
		String propertyName = buffer.readString();
		if(propertyName.equalsIgnoreCase("none")) {
			return null;
		}
		String propertyType = buffer.readString();
		switch(propertyType) {
			case "IntProperty":
				return readIntProperty(propertyName);
			case "StrProperty":
				return readStringProperty(propertyName);
			case "FloatProperty":
				return readFloatProperty(propertyName);
			case "NameProperty":
				return readNameProperty(propertyName);
			case "ArrayProperty":
				return readArrayProperty(propertyName);
			case "ByteProperty":
				return readByteProperty(propertyName);
			case "QWordProperty":
				return readIntProperty(propertyName);
			case "BoolProperty":
				return readBoolProperty(propertyName);
			default:
				throw new UnsupportedOperationException("Unable to parse property type " + propertyType);
		}
	}

	private HeaderProperty<?> readArrayProperty(String propertyName) {
		buffer.getLong(); // These are 8 unknown bytes apparently
		int arrayLength = buffer.getInt();
		List<List<HeaderProperty<?>>> arrayProperty = new ArrayList<>();
		for(int i = 0; i < arrayLength; ++i) {
			List<HeaderProperty<?>> props = readProperties();
			if(props != null) {
				arrayProperty.add(props);
			}
		}
		return new HeaderProperty<>(propertyName, arrayProperty, List.class);
	}

	private HeaderProperty<?> readByteProperty(String propertyName) {
		buffer.getLong(); // These are 8 unknown bytes apparently
		String byteKey = buffer.readString();
		String byteValue = buffer.readString();
		ByteProperty byteProperty = ByteProperty.of(byteKey, byteValue);
		return new HeaderProperty<>(propertyName, byteProperty, ByteProperty.class);
	}

	private HeaderProperty<?> readBoolProperty(String propertyName) {
		buffer.getLong(); // These are 8 unknown bytes apparently
		byte singleByte = buffer.get();
		if(singleByte == 0) {
			return new HeaderProperty<>(propertyName, new Boolean(false), Boolean.class);
		} else if(singleByte == 1) {
			return new HeaderProperty<>(propertyName, new Boolean(true), Boolean.class);
		}
		throw new UnsupportedOperationException("Unable to read boolean property from byte value " + singleByte);
	}

	private HeaderProperty<?> readNameProperty(String propertyName) {
		return readStringProperty(propertyName);
	}

	private HeaderProperty<?> readFloatProperty(String propertyName) {
		long floatLength = buffer.getLong();
		if(floatLength == 4L) {
			return new HeaderProperty<>(propertyName, buffer.getFloat(), Float.class);
		} else if(floatLength == 8L) {
			return new HeaderProperty<>(propertyName, buffer.getDouble(), Double.class);
		}
		throw new UnsupportedOperationException(
				"Unable to parse a floating point number of length " + Long.toString(floatLength));
	}

	private HeaderProperty<?> readStringProperty(String propertyName) {
		buffer.getLong(); // These are 8 unknown bytes apparently
		int length = buffer.getInt();
		String value;
		if(length < 0) {
			value = buffer.readStringUtf16(Math.abs(length));
		} else {
			value = buffer.readString(length);
		}
		return new HeaderProperty<String>(propertyName, value, String.class);
	}

	private HeaderProperty<?> readIntProperty(String propertyName) {
		long integerLength = buffer.getLong();
		if(integerLength == 1) {
			return new HeaderProperty<>(propertyName, new Character((char) buffer.get()), Character.class);
		} else if(integerLength == 2) {
			return new HeaderProperty<>(propertyName, new Short(buffer.getShort()), Short.class);
		} else if(integerLength == 4) {
			return new HeaderProperty<>(propertyName, new Integer(buffer.getInt()), Integer.class);
		} else if(integerLength == 8) {
			return new HeaderProperty<>(propertyName, new Long(buffer.getLong()), Long.class);
		}
		throw new UnsupportedOperationException("Integer type of length " + integerLength + " not supported.");
	}
}
