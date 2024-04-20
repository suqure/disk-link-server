package ltd.finelink.tool.disk.vo;

import java.util.Date;

import lombok.Data;

@Data
public class UserInfoVo {
	
	private String nickname;
	
	private String username;
	
	private String token;
	
	private String channel;
	
	private String avatar; 
	
	private String email;
	
	private Long createTime;

}
