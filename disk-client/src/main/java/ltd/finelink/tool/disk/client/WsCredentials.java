package ltd.finelink.tool.disk.client;

import ltd.finelink.tool.disk.base.Credentials;
import ltd.finelink.tool.disk.enums.WSProtocol;

public class WsCredentials implements Credentials {

	private String userId;

	private String token;

	private String channel;

	private String host;

	private int port;

	private String query;

	private WSProtocol protocol;

	private static final String URL_TEMPLATE = "%s:%s/%s/%s/%s";

	private static String DEFALUT_QUERY = "anonymous";

	private static WSProtocol DEFALUT_PROTOCOL = WSProtocol.WS;

	private static String DEFALUT_CHANNEL = "DEFALUT";

	public WsCredentials(String userId, String token, String host, int port) {
		this(userId, DEFALUT_CHANNEL, token, host, port, DEFALUT_PROTOCOL, DEFALUT_QUERY);
	}

	public WsCredentials(String userId, String channel, String token, String host, int port, WSProtocol protocol,
			String query) {
		this.userId = userId;
		this.token = token;
		this.channel = channel;
		this.host = host;
		this.port = port;
		this.query = query;
		this.protocol = protocol;
	}

	@Override
	public String getAccessToken() { 
		return token;
	}

	@Override
	public String getServerEndPoint() {

		return protocol.getValue() + String.format(URL_TEMPLATE, host, port, query, userId, channel);
	}

}
