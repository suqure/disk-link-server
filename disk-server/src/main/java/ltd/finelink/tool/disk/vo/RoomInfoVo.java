package ltd.finelink.tool.disk.vo;

import java.util.List;

import lombok.Data;

@Data
public class RoomInfoVo {

	private String code;

	private String name;

	private String ownerId;
	
	private String password;

	List<RoomUserVo> users;

}
