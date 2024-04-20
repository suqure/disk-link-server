package ltd.finelink.tool.disk.enums;
/**
 * 服务器消息类型
 * @author chenjinghe
 *
 */
public enum LogoutMessage {
	/**
	 * 挤掉线
	 */
	CONFLICT(409,"通道被占用"),
	/**
	 * 禁止通道
	 */
	EXCEPTION(500,"服务器异常"),
	/**
	 * 禁止通道
	 */
	FORBIDDEN(403,"通道被禁用");
	 
	private int code; 
	
	private String message;
	
	
	private LogoutMessage(int code,String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

}
