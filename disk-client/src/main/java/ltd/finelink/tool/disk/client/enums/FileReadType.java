package ltd.finelink.tool.disk.client.enums;
/**
 * 消息类型
 * @author chenjinghe
 *
 */
public enum FileReadType {
	
	/**
	 * 创建
	 */
	CREATE(-1),
	/**
	 * 读取
	 */
	READ(1),
	/**
	 * 关闭
	 */
	CLOSE(0);
	 
	private int code; 
	
	
	private FileReadType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
