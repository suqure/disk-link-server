package ltd.finelink.tool.disk.enums;
/**
 * 服务器消息类型
 * @author chenjinghe
 *
 */
public enum CustomBodyType {
	/**
	 * 种子消息
	 */
	TORRENT(0),
	/**
	 * 房间信息
	 */
	ROOM(1);
	 
	private int code; 
	
	
	private CustomBodyType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
