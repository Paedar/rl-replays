package rocketleague.replays.parser.metadata;

import java.util.Optional;

import rocketleague.replays.parser.util.ReplayByteBuffer;

public class ReplayVersion {
	private final int engineVersion;
	private final int licenseeVersion;
	private final Optional<Integer> netVersion;

	private ReplayVersion(int engineVersion, int licenseeVersion, Integer netVersion) {
		this.engineVersion = engineVersion;
		this.licenseeVersion = licenseeVersion;
		this.netVersion = Optional.ofNullable(netVersion);
	}

	public static ReplayVersion readFrom(ReplayByteBuffer buffer) {
		if(buffer == null) {
			throw new IllegalArgumentException("Buffer can't be null.");
		}
		int engineVersion = buffer.getInt();
		int licenseeVersion = buffer.getInt();
		Integer netVersion = engineVersion >= 868 && licenseeVersion >= 18 ? buffer.getInt() : null;

		return new ReplayVersion(engineVersion, licenseeVersion, netVersion);
	}

	public int getEngineVersion() {
		return engineVersion;
	}

	public int getLicenseeVersion() {
		return licenseeVersion;
	}

	public Optional<Integer> getNetVersion() {
		return netVersion;
	}

	public boolean hasNetVersion() {
		return netVersion.isPresent();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + engineVersion;
		result = prime * result + licenseeVersion;
		result = prime * result + netVersion.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		ReplayVersion other = (ReplayVersion) obj;
		if(engineVersion != other.engineVersion)
			return false;
		if(licenseeVersion != other.licenseeVersion)
			return false;
		if(netVersion == null) {
			if(other.netVersion != null)
				return false;
		} else if(!netVersion.equals(other.netVersion))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String netVersionString = netVersion.map((nv) -> "." + nv.toString()).orElse("");
		return "ReplayVersion [" + engineVersion + "." + licenseeVersion + netVersionString + "]";
	}

}
