package ltd.finelink.tool.disk.client.enums;
/**
 * webrtc交换信息类型
 * @author chenjinghe
 *
 */
public enum WebRTCType {
	/**
	 * 申请
	 */
	APPLY("apply"),
	/**
	 * 授权
	 */
	OFFER("offer"),
	/**
	 * 响应
	 */
	ANSWER("answer"),
	/**
	 * ICE信息
	 */
	ICE("_ice"),
	/**
	 * 认证
	 */
	AUTH("auth"),

	/**
	 * 关闭连接
	 */
	CLOSE("close");
	 
	private String value; 
	
	
	private WebRTCType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
