package ltd.finelink.tool.disk.enums;
/**
 * 登录类型
 * @author chenjinghe
 *
 */
public enum LoginType {
	/**
	 * 账号登录
	 */
	USERNAME(0),
	/**
	 * 邮箱登录
	 */
	EMAIL(1);
	 
	private int code; 
	
	
	private LoginType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
