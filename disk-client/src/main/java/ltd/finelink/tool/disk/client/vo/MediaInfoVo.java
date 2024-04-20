package ltd.finelink.tool.disk.client.vo;

import lombok.Data;

@Data
public class MediaInfoVo {
	
	private boolean desktop;
	
	private boolean video;
	
	private boolean audio;
	
	private boolean videoCall;
	
	private boolean audioCall;
	
	private String code;
	
	private String message;
	
	private int type;

}
