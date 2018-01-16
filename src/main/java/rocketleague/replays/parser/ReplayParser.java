package rocketleague.replays.parser;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReplayParser {

	private ByteBuffer buffer;

	public ReplayParser(RawData data) {
		this.buffer = ByteBuffer.wrap(data.getRawBytes());
	}

	public void parse() {
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

		List<Property<?>> properties = readProperties();
		System.out.println(properties);
	}

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
				// elif type_name == 'ArrayProperty':
				// # I imagine that this is the length of bytes that the data
				// # in the "array" actually take up in the file.
				// unknown = self._read_integer(replay_file, 8)
				// array_length = self._read_integer(replay_file)
				//
				// value = [
				// self._read_properties(replay_file)
				// for x in xrange(array_length)
				// ]

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

			case "ByteProperty":
				// elif type_name == 'ByteProperty':
				// # This could be a new array type.
				// # 25 (8) / 15 (4) / Str len 15 / Int (4) - 21 / Str len 21
				//
				// self._read_integer(replay_file, 8)
				// key_length = self._read_integer(replay_file, 4)
				// byte_key = self._read_string(replay_file, length=key_length)
				// byte_value = self._read_string(replay_file)
				//
				// value = {
				// byte_key: byte_value
				// }
				buffer.getLong(); // These are 8 unknown bytes apparently
				String byteKey = readString();
				String byteValue = readString();
				ByteProperty byteProperty = ByteProperty.of(byteKey, byteValue);
				return new Property<>(propertyName, byteProperty, ByteProperty.class);

			case "QWordProperty":
				// elif type_name == 'QWordProperty':
				// # 64 bit int, 8 bytes.
				// length = self._read_integer(replay_file, 8)
				// value = self._read_integer(replay_file, length)
				return readIntProperty(propertyName);

			case "BoolProperty":
				// elif type_name == 'BoolProperty':
				// unknown = self._read_integer(replay_file, 8)
				// value = self._read_integer(replay_file, 1)
				//
				// if value == 0:
				// value = False
				// elif value == 1:
				// value = True
				buffer.getLong(); // These are 8 unknown bytes apparently
				byte singleByte = buffer.get();
				if(singleByte == 0) {
					return new Property<>(propertyName, new Boolean(false), Boolean.class);
				} else if(singleByte == 1) {
					return new Property<>(propertyName, new Boolean(true), Boolean.class);
				}
				throw new UnsupportedOperationException("Unable to read boolean property from byte value " + singleByte);
				
			default:
				throw new UnsupportedOperationException("Unable to parse property type " + propertyType);

		}
	}

	private Property<?> readNameProperty(String propertyName) {
		// elif type_name == 'NameProperty':
		// unknown = self._read_integer(replay_file, 8)
		// value = self._read_string(replay_file)
		buffer.getLong(); // These are 8 unknown bytes apparently
		return new Property<>(propertyName, readString(), String.class);
	}

	private Property<?> readFloatProperty(String propertyName) {
		// elif type_name == 'FloatProperty':
		// length = self._read_integer(replay_file, 8)
		// value = self._read_float(replay_file, length)
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
		// elif type_name == 'StrProperty':
		// unknown = self._read_integer(replay_file, 8)
		// length = self._read_integer(replay_file)
		//
		// if length < 0:
		// length = abs(length) * 2
		// value = self._read_string(replay_file,
		// length)[:-1].decode('utf-16').encode('utf-8')
		// else:
		// value = self._read_string(replay_file, length)
		buffer.getLong(); // These are 8 unknown bytes apparently
		int length = buffer.getInt();
		String value;
		if(length < 0) {
			value = readString(Math.abs(length) * 2);
			// Do re-encoding if necessary at all.
		} else {
			value = readString(length);
		}
		return new Property<String>(propertyName, value, String.class);
	}

	private Property<?> readIntProperty(String propertyName) {
		// if type_name == 'IntProperty':
		// value_length = self._read_integer(replay_file, 8)
		// value = self._read_integer(replay_file, value_length)
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
