package ltd.finelink.tool.disk.client.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SubcribeVo {

	private String code;

	private String userId;

	private String channel;

	private String device;

	private Integer online;

	private Integer status;

	private String version;

	private Boolean local;

	private String password;

	private Long rtt;

	private List<FileInfo> files = new ArrayList<>();
	
	private DirInfo dir;
	
	public DirInfo getDir() {
		if(dir==null) {
			this.dir = new DirInfo();
			dir.setId(code);
			dir.setDevice(code);
			dir.setName(code);
			dir.setRoot("");
			dir.setPath("");
			dir.setType(0);
			
		}
		return dir;
	}

}
