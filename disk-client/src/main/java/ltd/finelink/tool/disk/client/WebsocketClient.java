package ltd.finelink.tool.disk.client;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import ltd.finelink.tool.disk.base.Credentials;
import ltd.finelink.tool.disk.protobuf.ClientMessage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebsocketClient {

	private static long CONNECT_TIMEOUT_SECOND = 30;

	private static long WRITE_TIMEOUT_SECOND = 60;

	private static long READ_TIMEOUT_SECOND = 60;

	private static long PING_INTERVAL_SECOND = 30;

	public static final String TOKEN = "token";

	private static final String USER_AGENT = "Desktop";

	private OkHttpClient client;

	private WebSocket socket;

	private Credentials credentials;

	protected static volatile int reconnectCount = 0;

	protected static volatile boolean connect = false;

	protected static volatile boolean tryConnect = false;

	public static volatile WebsocketClient currentInstance;

	protected static volatile boolean reconnecting = false;

	public WebsocketClient(Credentials credentials) {

		this.credentials = credentials;
		client = new OkHttpClient.Builder().connectTimeout(CONNECT_TIMEOUT_SECOND, TimeUnit.SECONDS)
				.writeTimeout(WRITE_TIMEOUT_SECOND, TimeUnit.SECONDS).readTimeout(READ_TIMEOUT_SECOND, TimeUnit.SECONDS)
				.pingInterval(PING_INTERVAL_SECOND, TimeUnit.SECONDS).build();
		if (currentInstance != null) {
			currentInstance.close();
		}
		currentInstance = this;
	}

	public void start(WebSocketListener listener) {
		client.dispatcher().cancelAll();
		Builder builder = new Request.Builder();
		if (StringUtils.isNotBlank(credentials.getAccessToken())) {
			builder.addHeader(TOKEN, credentials.getAccessToken());
		}
		Request request = builder.url(credentials.getServerEndPoint()).addHeader("User-Agent", USER_AGENT).build();
		socket = client.newWebSocket(request, listener);
		tryConnect = true;

	}

	public void changeCredentials(Credentials credentials) {
		this.credentials = credentials;
		if (connect) {
			socket.close(1000, "Change Credentials");
		}

	}

	public void reconnect(WebSocketListener listener) {
		if (reconnecting) {
			return;
		}
		reconnecting = true; 
		new Thread(() -> {
			try {
				if (reconnectCount > 20) {
					Thread.sleep(600000);
				} else {
					Thread.sleep(30000 * reconnectCount);
				}
				reconnectCount++;
			} catch (InterruptedException e) {

			}
			if (tryConnect && !connect) {
				currentInstance.start(listener);
			}
		}).start();
	}

	protected void sendMessage(byte[] data) {
		if (connect) {
			socket.send(ByteString.of(data));
		}
	}

	protected void sendMessage(String data) {
		if (connect) {
			socket.send(data);
		}
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	public void sendClientMessage(ClientMessage message) {
		sendMessage(message.toByteArray());
	}

	public void close() {
		if (socket != null) {
			socket.close(1000, null);
		}
		tryConnect = false;
	}

	public WebSocket getCurrentSocket() {
		return socket;
	}

}
