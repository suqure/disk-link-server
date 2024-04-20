package ltd.finelink.tool.disk.enums;
/**
 * WEBRTC消息类型
 * @author chenjinghe
 *
 */
public enum WebRTCBodyType {
	/**
	 * 申请 
	 */
	APPLY(0),
	/**
	 * 响应申请
	 */
	REPLY(1),
	/**
	 * 拒绝连接
	 */
	REJECT(-1),
	/**
	 * 传输消息
	 */
	MESSAGE(2),
	/**
	 * 关闭
	 */
	CLOSE(3);
	 
	private int code; 
	
	
	private WebRTCBodyType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
