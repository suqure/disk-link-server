package ltd.finelink.tool.disk.client.enums;
/**
 * 消息类型
 * @author chenjinghe
 *
 */
public enum ChannelMessageType {
	/**
	 * 基本消息
	 */
	BASIC(0),
	/**
	 * 文件消息
	 */
	FILE(1),
	/**
	 * 目录消息
	 */
	DIR(2),
	/**
	 * 媒体消息
	 */
	MEDIA(3),
	/**
	 * 聊天消息
	 */
	CHAT(4);
	 
	private int code; 
	
	
	private ChannelMessageType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
