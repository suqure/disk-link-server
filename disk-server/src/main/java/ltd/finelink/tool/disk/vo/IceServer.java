package ltd.finelink.tool.disk.vo;

import lombok.Data;

@Data
public class IceServer {

	private String urls;

	private String username;

	private String credential;

	public static IceServer defalut() {
		IceServer server = new IceServer();
		server.setUrls("stun:stun.finelink.ltd:5349"); 
		return server;
	}

}
