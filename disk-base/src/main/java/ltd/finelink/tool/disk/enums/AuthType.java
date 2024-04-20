package ltd.finelink.tool.disk.enums;
/**
 * 服务器消息类型
 * @author chenjinghe
 *
 */
public enum AuthType {
	/**
	 * 账号认证 
	 */
	USERNAME(0),
	/**
	 * 手机号认证
	 */
	PHONE(1),
	/**
	 * 微信认证
	 */
	WECHAT(2);
	 
	private int code; 
	
	
	private AuthType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
