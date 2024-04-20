package ltd.finelink.tool.disk.enums;
/**
 * 邮箱认证类型
 * @author chenjinghe
 *
 */
public enum EmailVerifyType {
	/**
	 * 注册 
	 */
	REG(0),
	/**
	 * 登录
	 */
	lOGIN(1),
	/**
	 * 重置密码
	 */
	RESET(2);
	 
	private int code; 
	
	
	private EmailVerifyType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
	
	public static EmailVerifyType parese(int code) {
		for(EmailVerifyType type:EmailVerifyType.values()) {
			if(type.getCode()==code) {
				return type;
			}
		}
		return null;
	}

}
