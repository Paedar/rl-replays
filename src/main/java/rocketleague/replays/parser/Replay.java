package rocketleague.replays.parser;

import rocketleague.replays.parser.metadata.Header;
import rocketleague.replays.parser.metadata.ReplayVersion;
import rocketleague.replays.parser.util.RawData;

public class Replay {
	private final RawData rawData;
	private final RawData rawNetworkStream;
	private final Header header;

	private Replay(RawData rawData,
			RawData rawNetworkStream,
			Header header) {
		this.rawData = rawData;
		this.rawNetworkStream = rawNetworkStream;
		this.header = header;
	}

	public static Builder getBuilder() {
		return new Builder();
	}

	public static class Builder {
		private RawData builderRawData;
		private RawData builderRawNetworkStream;
		private Header builderHeader;
		
		public Builder() {
			// NOOP
		}

		public Replay build() {
			return new Replay(
					builderRawData,
					builderRawNetworkStream,
					builderHeader);
		}

		public Builder rawData(RawData data) {
			this.builderRawData = data;
			return this;
		}

		public Builder rawNetWorkStream(RawData data) {
			this.builderRawNetworkStream = data;
			return this;
		}
		
		public Builder header(Header header) {
			this.builderHeader = header;
			return this;
		}

	}

}
