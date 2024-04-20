package ltd.finelink.tool.disk.client.vo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import ltd.finelink.tool.disk.client.context.UserContext;
import ltd.finelink.tool.disk.client.entity.ShareDir;

@Data
public class DirInfo {

	private String id;

	private String root;

	private String device;

	private int type;

	private String name;

	private long size;

	private String path;

	private List<DirInfo> children;

	public static DirInfo transferToDirInfo(ShareDir root) {
		DirInfo dir = new DirInfo();
		dir.setId(root.getKey());
		dir.setName(root.getName());
		dir.setRoot(root.getKey());
		dir.setPath(File.separator);
		dir.setDevice(UserContext.currentUser.getCode());
		dir.setType(0);
		return dir;
	}

	public void addChildren(DirInfo child) {
		if (children == null) {
			children = new ArrayList<>();
		}
		children.add(child);
	}

}
