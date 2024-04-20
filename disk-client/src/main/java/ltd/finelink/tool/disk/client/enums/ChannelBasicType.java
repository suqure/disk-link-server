package ltd.finelink.tool.disk.client.enums;

/**
 * 消息类型
 * 
 * @author chenjinghe
 *
 */
public enum ChannelBasicType {
	/**
	 * ping/pong消息
	 */
	BASIC(0),
	/**
	 * 文件列表消息
	 */
	FILELIST(1),
	/**
	 * 下载请求
	 */
	DOWNLOAD(2),
	/**
	 * 下载文件信息
	 */
	FILECHUNK(3),
	/**
	 * 暂停下载
	 */
	STOP(4);

	private int code;

	private ChannelBasicType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
