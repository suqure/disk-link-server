package ltd.finelink.tool.disk.client.enums;

/**
 * 消息类型
 * 
 * @author chenjinghe
 *
 */
public enum DownloadFileType {
	/**
	 * 接收下载
	 */
	ACCEPT(0),
	/**
	 * 手动下载
	 */
	MANUAL(1),
	/**
	 * 目录下载
	 */
	DIR(2);

	private int code;

	private DownloadFileType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
