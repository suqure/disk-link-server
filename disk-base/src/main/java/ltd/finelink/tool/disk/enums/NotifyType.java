package ltd.finelink.tool.disk.enums;
/**
 * 服务器消息类型
 * @author chenjinghe
 *
 */
public enum NotifyType {
	/**
	 * 系统通知
	 */
	SYSTEM("system"),
	/**
	 * 评论通知
	 */
	COMMENT("user_comment");
	 
	private String code; 
	
	
	private NotifyType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

}
