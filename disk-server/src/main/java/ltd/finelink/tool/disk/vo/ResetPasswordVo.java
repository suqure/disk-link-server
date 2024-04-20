package ltd.finelink.tool.disk.vo;

import lombok.Data;

@Data
public class ResetPasswordVo {
	
	private int type;
	
	private String login;
	
	private String password; 
	
	private String verify;

}
