package ltd.finelink.tool.disk.client.vo;

import dev.onvoid.webrtc.RTCIceServer;
import lombok.Data;

@Data
public class IceServer {

	private String urls;
	
	private String hostname;

	private String username;

	private String credential;

	public static IceServer defalut() {
		IceServer server = new IceServer();
		server.setUrls("stun:stun.l.google.com:19302");
		return server;
	}

	public RTCIceServer tranfer() {
		RTCIceServer iceServer = new RTCIceServer();
		iceServer.urls.add(urls);
		iceServer.username = username;
		iceServer.password = credential;
		iceServer.hostname = hostname;
		return iceServer;
	}

}
