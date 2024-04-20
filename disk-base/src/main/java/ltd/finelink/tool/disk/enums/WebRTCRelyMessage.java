package ltd.finelink.tool.disk.enums;
/**
 * WebRTC错误消息类型
 * @author chenjinghe
 *
 */
public enum WebRTCRelyMessage {
	/**
	 * 不在线
	 */
	OFFLINE(404,"user not online"),
	/**
	 * 用户拒绝
	 */
	FORBIDDEN(403,"user reject connect");
	 
	private int code; 
	
	private String message;
	
	
	private WebRTCRelyMessage(int code,String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

}
