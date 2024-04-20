package ltd.finelink.tool.disk.client.vo;

import dev.onvoid.webrtc.RTCIceConnectionState;
import lombok.Data;
@Data
public class ConnectionMessage {
	
	private String code;
	
	private RTCIceConnectionState state;

	public ConnectionMessage(String code, RTCIceConnectionState state) {
		this.code = code;
		this.state = state;
	}
	
	

}
