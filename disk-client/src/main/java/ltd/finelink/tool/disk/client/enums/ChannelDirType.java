package ltd.finelink.tool.disk.client.enums;
/**
 * 目录消息类型
 * @author chenjinghe
 *
 */
public enum ChannelDirType {
	/**
	 * 请求目录
	 */
	REQ(0),
	/**
	 * 响应请求
	 */
	DIR(1),
	/**
	 * 预下载确认
	 */
	FILE(2),
	/**
	 * 确认信息
	 */
	CONFIRM(3),
	/**
	 * 错误信息
	 */
	ERROR(4);
	 
	private int code; 
	
	
	private ChannelDirType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
