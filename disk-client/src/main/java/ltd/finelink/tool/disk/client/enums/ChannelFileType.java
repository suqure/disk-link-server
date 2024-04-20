package ltd.finelink.tool.disk.client.enums;
/**
 * 消息类型
 * @author chenjinghe
 *
 */
public enum ChannelFileType {
	/**
	 * 文件chunk数据
	 */
	CHUNK(0),
	/**
	 * 文件发送请求
	 */
	SEND(1),
	/**
	 * 确认并直接接收文件
	 */
	ACCEPT(2),
	/**
	 * 拒绝文件
	 */
	REJECT(3),
	/**
	 * 确认接收接收文件信息
	 */
	CONFIRM(4),
	/**
	 * 接收目录文件
	 */
	DIR(5);
	 
	private int code; 
	
	
	private ChannelFileType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
