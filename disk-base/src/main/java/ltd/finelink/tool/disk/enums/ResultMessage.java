package ltd.finelink.tool.disk.enums;
/**
 * 响应消息
 * @author chenjinghe
 *
 */
public enum ResultMessage {
	 
	SUCCESS(200,"OK"),

	EXPIRED(401, "TOKEN EXPIRED"),
	 
	EXCEPTION(500,"SERVER EXCEPTION"),
	 
	FORBIDDEN(403,"NO PERMISSION"),
	
	PARAMETER_ERROR(501,"PARAMETER ERROR");
	 
	private int code; 
	
	private String message;
	
	
	private ResultMessage(int code,String message) {
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
