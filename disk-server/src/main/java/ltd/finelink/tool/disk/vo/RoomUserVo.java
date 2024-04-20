package ltd.finelink.tool.disk.vo;

import lombok.Data;

@Data
public class RoomUserVo {
  
	private String userId;

	private String channel;

	private String device;
	
	private String region;
	
	private String nickname;
	
	private Boolean leave;

}
