package rocketleague.replays.parser;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ReplayParser {

	private ByteBuffer buffer;

	public ReplayParser(RawData data) {
		this.buffer = ByteBuffer.wrap(data.getRawBytes());
	}

	public void parse() {
		System.out.println("Buffer limit: " + buffer.limit());

		buffer.order(ByteOrder.LITTLE_ENDIAN);
		int propertiesLength = buffer.getInt();
		System.out.format("Properties length: %d\n", propertiesLength);
		byte[] crc = new byte[4];
		buffer.get(crc);
		System.out.format("CRC: %s.%s.%s.%s\n", Byte.toString(crc[0]), Byte.toString(crc[1]), Byte.toString(crc[2]),
				Byte.toString(crc[3]));
		String versionNumber = String.format("%d.%d", buffer.getInt(), buffer.getInt());
		System.out.println(versionNumber);
		String version = readString();
		System.out.format("Version string: %s\n", version);

		List<Property<?>> headerProperties = readProperties();
		System.out.println(headerProperties);

		System.out.println("Buffer position: " + buffer.position());

		int remainingLength = buffer.getInt();
		System.out.println("Remaining length of data: " + remainingLength);
		byte[] crc2 = new byte[4];
		buffer.get(crc2);
		System.out.format("CRC2: %s.%s.%s.%s\n", Byte.toString(crc2[0]), Byte.toString(crc2[1]), Byte.toString(crc2[2]),
				Byte.toString(crc2[3]));

		List<String> maps = readLevelInfo();
		System.out.println(maps);
		System.out.println("Buffer position: " + buffer.position());

		List<KeyFrame> keyFrames = readKeyFrames();
		System.out.println(keyFrames);
		System.out.println("Buffer position: " + buffer.position());

		RawData networkStream = readNetworkStream();
		System.out.println("Network stream length: " + networkStream.length());
		System.out.println("Buffer position: " + buffer.position());

		List<DebugString> debugStrings = readDebugStrings();
		System.out.println(debugStrings);
		System.out.println("Buffer position: " + buffer.position());

		List<GoalTick> goalTicks = readGoalTicks();
		System.out.println(goalTicks);
		System.out.println("Buffer position: " + buffer.position());

		List<String> packages = readPackages();
		System.out.println(packages);
		System.out.println("Buffer position: " + buffer.position());

		// data['objects'] = self._read_objects(replay_file)
		List<String> objects = readObjects();
		System.out.println(objects);
		System.out.println("Buffer position: " + buffer.position());

		// data['name_table'] = self._read_name_table(replay_file)
		List<String> nameTable = readNameTable();
		System.out.println(nameTable);
		System.out.println("Buffer position: " + buffer.position());

		// data['classes'] = self._read_classes(replay_file)
		Map<Integer, String> classes = readClasses();
		System.out.println(classes);
		System.out.println("Buffer position: " + buffer.position());

		// data['property_tree'] = self._read_property_tree(replay_file,
		// data['objects'], data['classes'])

		// assert replay_file.tell() == properties_length + remaining_length + 16

		// # Run some manual parsing operations.
		// data = self.manual_parse(data, replay_file)

	}

	private List<String> readLevelInfo() {
		return readStringList();
	}

	private List<String> readStringList() {
		return Collections.nCopies(buffer.getInt(), "")
				.stream()
				.map(o -> readString())
				.collect(Collectors.toList());
	}

	private List<KeyFrame> readKeyFrames() {
		return Collections.nCopies(buffer.getInt(), (KeyFrame) null)
				.stream()
				.map(o -> readKeyFrame())
				.collect(Collectors.toList());
	}

	private KeyFrame readKeyFrame() {
		return KeyFrame.of(buffer.getFloat(), buffer.getInt(), buffer.getInt());
	}

	private RawData readNetworkStream() {
		int streamLength = buffer.getInt();
		return RawData.createFrom(readBytes(streamLength));
	}

	private List<DebugString> readDebugStrings() {
		// def _read_debug_strings(self, replay_file):
		// array_length = self._read_integer(replay_file)
		//
		// if array_length == 0:
		// return []
		//
		// debug_strings = []
		//
		// unknown = self._read_integer(replay_file)
		//
		// while len(debug_strings) < array_length:
		// player_name = self._read_string(replay_file)
		// debug_string = self._read_string(replay_file)
		//
		// debug_strings.append({
		// 'PlayerName': player_name,
		// 'DebugString': debug_string,
		// })
		//
		// if len(debug_strings) < array_length:
		// # Seems to be some nulls and an ACK?
		// unknown = self._read_integer(replay_file)
		//
		// return debug_strings
		int numberOfDebugStrings = buffer.getInt();
		if(numberOfDebugStrings == 0) {
			return Collections.emptyList();
		}
		buffer.getInt(); // Some unknown data
		return Collections.nCopies(numberOfDebugStrings, (DebugString) null)
				.stream()
				.map(o -> DebugString.of(
						readString(),
						readString()))
				.collect(Collectors.toList());
	}

	private List<GoalTick> readGoalTicks() {
		int numberOfGoals = buffer.getInt();
		System.out.println("Number of goals: " + numberOfGoals);
		return Collections.nCopies(numberOfGoals, (GoalTick) null)
				.stream()
				.map(o -> GoalTick.of(readString(), buffer.getInt()))
				.collect(Collectors.toList());
	}

	private List<String> readPackages() {
		return readStringList();
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
		return readStringList();
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
		return readStringList();
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
					String name = readString();
					int id = buffer.getInt();
					classes.put(id, name);
				});

		return classes;
	}

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
	//
	// # Map the property keys against the class list.
	// classed = {}

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

	// for branch in branches:
	// # {'parent_id': 36, 'properties': {42: 36}, 'class': 43, 'id': 37}
	// classed[branch['class']] = {
	// 'class': classes[branch['class']],
	// 'properties': map_properties(branch['id'] if branch['id'] > 0 else
	// branch['parent_id'])
	// }
	//
	// return branches

	// # Temporary method while we learn the replay format.
	// def manual_parse(self, results, replay_file):
	// server_regexp = re.compile(self.SERVER_REGEX)
	//
	// replay_file.seek(0)
	// search = server_regexp.search(replay_file.read())
	// if search:
	// results['header']['ServerName'] = search.group()
	//
	// return results

	private List<Property<?>> readProperties() {
		List<Property<?>> properties = new ArrayList<>();
		Property<?> property;
		do {
			property = readProperty();
			if(property != null) {
				properties.add(property);
			}
		} while(property != null);
		return properties;
	}

	private Property<?> readProperty() {
		String propertyName = readString();
		if(propertyName.equalsIgnoreCase("none")) {
			return null;
		}
		String propertyType = readString();
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

	private Property<?> readArrayProperty(String propertyName) {
		buffer.getLong(); // These are 8 unknown bytes apparently
		int arrayLength = buffer.getInt();
		List<List<Property<?>>> arrayProperty = new ArrayList<>();
		for(int i = 0; i < arrayLength; ++i) {
			List<Property<?>> props = readProperties();
			if(props != null) {
				arrayProperty.add(props);
			}
		}
		return new Property<>(propertyName, arrayProperty, List.class);
	}

	private Property<?> readByteProperty(String propertyName) {
		buffer.getLong(); // These are 8 unknown bytes apparently
		String byteKey = readString();
		String byteValue = readString();
		ByteProperty byteProperty = ByteProperty.of(byteKey, byteValue);
		return new Property<>(propertyName, byteProperty, ByteProperty.class);
	}

	private Property<?> readBoolProperty(String propertyName) {
		buffer.getLong(); // These are 8 unknown bytes apparently
		byte singleByte = buffer.get();
		if(singleByte == 0) {
			return new Property<>(propertyName, new Boolean(false), Boolean.class);
		} else if(singleByte == 1) {
			return new Property<>(propertyName, new Boolean(true), Boolean.class);
		}
		throw new UnsupportedOperationException("Unable to read boolean property from byte value " + singleByte);
	}

	private Property<?> readNameProperty(String propertyName) {
		return readStringProperty(propertyName);
	}

	private Property<?> readFloatProperty(String propertyName) {
		long floatLength = buffer.getLong();
		if(floatLength == 4L) {
			return new Property<>(propertyName, buffer.getFloat(), Float.class);
		} else if(floatLength == 8L) {
			return new Property<>(propertyName, buffer.getDouble(), Double.class);
		}
		throw new UnsupportedOperationException(
				"Unable to parse a floating point number of length " + Long.toString(floatLength));
	}

	private Property<?> readStringProperty(String propertyName) {
		buffer.getLong(); // These are 8 unknown bytes apparently
		int length = buffer.getInt();
		String value;
		if(length < 0) {
			value = readString(Math.abs(length) * 2);
			// Do re-encoding if necessary at all.
			System.err.println("Re-encoding of property " + propertyName + " necessary?");
		} else {
			value = readString(length);
		}
		return new Property<String>(propertyName, value, String.class);
	}

	private Property<?> readIntProperty(String propertyName) {
		long integerLength = buffer.getLong();
		if(integerLength == 1) {
			return new Property<>(propertyName, new Character((char) buffer.get()), Character.class);
		} else if(integerLength == 2) {
			return new Property<>(propertyName, new Short(buffer.getShort()), Short.class);
		} else if(integerLength == 4) {
			return new Property<>(propertyName, new Integer(buffer.getInt()), Integer.class);
		} else if(integerLength == 8) {
			return new Property<>(propertyName, new Long(buffer.getLong()), Long.class);
		}
		throw new UnsupportedOperationException("Integer type of length " + integerLength + " not supported.");
	}

	private String readString() {
		int length = buffer.getInt();
		return readString(length);
	}

	private String readString(int length) {
		byte[] data = readBytes(length);
		String stringRead = new String(Arrays.copyOf(data, length - 1));
		return stringRead;
	}

	private byte[] readBytes(int length) {
		byte[] data = new byte[length];
		buffer.get(data);
		return data;
	}
}
