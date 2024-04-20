package ltd.finelink.tool.disk.vo;

import lombok.Data;

@Data
public class FileVo {
	
	private String code;
	
	private String username;
	
	private String channel;
	
	private String device;
	
	private String fileName;

    private String fullPath;
     
    private Long fileSize;

}
