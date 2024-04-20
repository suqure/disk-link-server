package ltd.finelink.tool.disk.enums;
/**
 * 服务器消息类型
 * @author chenjinghe
 *
 */
public enum RoomType {
	/**
	 * 新建房间
	 */
	CREATE("create"),
	/**
	 * 验证
	 */
	VERIFY("verify"),
	/**
	 * 加入房间
	 */
	JOIN("join"),
	/**
	 * 退出房间
	 */
	EXIT("exit"),
	/**
	 * 离开房间
	 */
	LEAVE("leave"),
	/**
	 *房间信息
	 */ 
	INFO("info"),
	/**
	 * 关闭房间
	 */
	CLOSE("close");
	 
	private String code; 
	
	
	private RoomType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

}
