package ltd.finelink.tool.disk.client.enums;

/**
 * 消息类型
 * 
 * @author chenjinghe
 *
 */
public enum ChannelMediaType {
	/**
	 * 设备媒体信息
	 */
	INFO(0),
	/**
	 * 桌面媒体请求
	 */
	DESKTOP(1),
	/**
	 * 视频媒体请求
	 */
	VIDEO(2),
	/**
	 * 音频媒体直播
	 */
	AUDIO(3);

	private int code;

	private ChannelMediaType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
