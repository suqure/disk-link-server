package ltd.finelink.tool.disk.enums;
/**
 * 协议
 * @author chenjinghe
 *
 */
public enum WSProtocol {
	/**
	 * ws协议
	 */
	WS("ws://"),
	/**
	 * wss协议
	 */
	WSS("wss://");
	 
	private String value; 
	
	
	private WSProtocol(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
