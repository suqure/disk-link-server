package ltd.finelink.tool.disk.exception;

import ltd.finelink.tool.disk.enums.BizErrorCode;

/**
 * 业务异常
 * @author chenjinghe
 *
 */
public class BizException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;

	private int intCode;

	public BizException(String code, String message) {
		super(message);
		this.code = code;
	}
	
	public BizException(BizErrorCode error) {
		super(error.getMessage());
		this.code = error.getCode();
	}

	public BizException(Integer intCode, String message) {
		super(message);
		this.intCode = intCode;
	}
 
	public BizException(String message, Throwable cause) {
		super(message, cause);
		this.code = BizErrorCode.UNKNOWN_ERROR.getCode();
	}

	public BizException(String message) {
		super(message);
		this.code = BizErrorCode.UNKNOWN_ERROR.getCode();
	}

	public BizException(Throwable cause) {
		super(cause);
		this.code = BizErrorCode.UNKNOWN_ERROR.getCode();
	}

	public String getCode() {
		if(null == this.code || code.isEmpty()){
			this.code = String.valueOf(this.intCode);
		}
		return this.code;
	}

	public int getIntCode() {
		return intCode != 0 ? intCode : Integer.parseInt(this.code.replaceAll("\\D", ""));
	}
	
}
