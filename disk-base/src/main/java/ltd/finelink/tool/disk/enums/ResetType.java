package ltd.finelink.tool.disk.enums;
/**
 * 验证类型
 * @author chenjinghe
 *
 */
public enum ResetType {
	/**
	 * 账号验证
	 */
	PASSWORD(0),
	/**
	 * 邮箱验证
	 */
	EMAIL(1);
	 
	private int code; 
	
	
	private ResetType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
