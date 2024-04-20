package ltd.finelink.tool.disk.client.vo;

import lombok.Data;

@Data
public class FileInfo {
	
	private String id;
	
	private String device;
	
	private String name;
	
	private long size;
	
	private long total;
	
	private long current;
	
	private long chunks;
	
	private int chunkSize;
	
	private String chunk;
	
	private String format;
	
	private int status;

}
