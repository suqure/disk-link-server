package ltd.finelink.tool.disk.enums;
/**
 * 服务器消息类型
 * @author chenjinghe
 *
 */
public enum SystemBodyType {
	/**
	 * 注销通知 
	 */
	LOGOUT(0),
	/**
	 * 确认收到消息
	 */
	CONFIRM(1),
	/**
	 * 通知消息
	 */
	NOTIFY(2),
	/**
	 * 拒绝消息
	 */
	REJECT(3),
	/**
	 * 心跳回复
	 */
	HEATBEAT(4);
	 
	private int code; 
	
	
	private SystemBodyType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
