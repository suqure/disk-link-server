package ltd.finelink.tool.disk.enums;
/**
 * 订阅消息类型
 * @author chenjinghe
 *
 */
public enum SubscribeBodyType {
	/**
	 * 创建订阅 
	 */
	CREATE(0),
	/**
	 * 订阅
	 */
	SUB(1), 
	/**
	 * 取消订阅
	 */
	UNSUB(2),
	/**
	 * 订阅消息
	 */
	MESSAGE(3),
	
	/**
	 * 关闭订阅码
	 */
	CLOSE(4);
	 
	private int code; 
	
	
	private SubscribeBodyType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
