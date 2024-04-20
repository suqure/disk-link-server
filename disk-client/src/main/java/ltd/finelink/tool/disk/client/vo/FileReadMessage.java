package ltd.finelink.tool.disk.client.vo;

import lombok.Data;

@Data
public class FileReadMessage {
	
	private int type; 
	
	FileInfo file;

}
