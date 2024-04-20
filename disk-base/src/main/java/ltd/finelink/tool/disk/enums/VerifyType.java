package ltd.finelink.tool.disk.enums;
/**
 * 验证类型
 * @author chenjinghe
 *
 */
public enum VerifyType {
	/**
	 * 账号验证
	 */
	USERNAME(0),
	/**
	 * 邮箱验证
	 */
	EMAIL(1);
	 
	private int code; 
	
	
	private VerifyType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
