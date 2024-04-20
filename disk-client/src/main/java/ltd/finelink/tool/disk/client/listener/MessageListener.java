package ltd.finelink.tool.disk.client.listener;

import ltd.finelink.tool.disk.protobuf.ServerMessage;
import okhttp3.WebSocket;

public interface MessageListener {
	
	void onMessage(WebSocket webSocket,ServerMessage message);
	
	void onMessage(WebSocket webSocket,String message); 
	
	void onOpen(WebSocket webSocket);

}
