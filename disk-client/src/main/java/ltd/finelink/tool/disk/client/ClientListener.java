/**
 * 
 */
package ltd.finelink.tool.disk.client;

import com.google.protobuf.InvalidProtocolBufferException;

import lombok.extern.slf4j.Slf4j;
import ltd.finelink.tool.disk.client.listener.MessageListener;
import ltd.finelink.tool.disk.protobuf.ServerMessage;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * @author suqur
 *
 */
@Slf4j
public class ClientListener extends WebSocketListener {

	private MessageListener listener;

	public ClientListener(MessageListener listener) {
		this.listener = listener;
	}

	@Override
	public void onClosed(WebSocket webSocket, int code, String reason) {
		if (WebsocketClient.connect) {
			WebsocketClient.connect = false;
			WebsocketClient.currentInstance.reconnect(this);
		}
		super.onClosed(webSocket, code, reason);
		log.info("websocket closed {} {}",code,reason);
	}

	@Override
	public void onClosing(WebSocket webSocket, int code, String reason) {
		super.onClosing(webSocket, code, reason);
		log.info("websocket closing {} {}",code,reason);
	}

	@Override
	public void onFailure(WebSocket webSocket, Throwable t, Response response) {
		WebsocketClient.connect = false;
		WebsocketClient.reconnecting = false;
		WebsocketClient.currentInstance.reconnect(this);
		
		log.error("websocket failure",t);
	}

	@Override
	public void onMessage(WebSocket webSocket, ByteString bytes) {

		try {
			listener.onMessage(webSocket,ServerMessage.parseFrom(bytes.toByteArray()));
		} catch (InvalidProtocolBufferException e) {
			log.error(e.getMessage(), e);
		}

	}

	@Override
	public void onMessage(WebSocket webSocket, String text) {
		listener.onMessage(webSocket,text);
	}

	@Override
	public void onOpen(WebSocket webSocket, Response response) {

		WebsocketClient.connect = true;
		WebsocketClient.reconnectCount = 0;
		if (WebsocketClient.reconnecting) {
			WebsocketClient.reconnecting = false;
		}
		listener.onOpen(webSocket);
		log.info("websocket connect");
	}

}
