package ltd.finelink.tool.disk.client.enums;
/**
 * 消息类型
 * @author chenjinghe
 *
 */
public enum NotifyType {
	/**
	 * 系统通知
	 */
	SYSTEM(0),
	/**
	 * 订阅通知
	 */
	SUBCRIBE(1),
	/**
	 * webrtc通知
	 */
	WEBRTC(2),
	/**
	 * 连接通知
	 */
	CONNECT(3),
	/**
	 * 通道通知
	 */
	CHANNEL(4),
	/**
	 * 文件上传通知
	 */
	UPLOAD(5);
	 
	private int code; 
	
	
	private NotifyType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
