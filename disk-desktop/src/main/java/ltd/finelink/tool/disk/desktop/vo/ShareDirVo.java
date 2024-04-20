package ltd.finelink.tool.disk.desktop.vo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Data;
import ltd.finelink.tool.disk.client.entity.ShareDir;

@Data
public class ShareDirVo {

	private Long id;

	private String name;

	private String key;

	private String path;

	private BooleanProperty checked = new SimpleBooleanProperty(false);

	public static ShareDirVo createShareDirVo(ShareDir shareDir) {
		ShareDirVo vo = new ShareDirVo();
		vo.setId(shareDir.getId());
		vo.setPath(shareDir.getPath());
		vo.setKey(shareDir.getKey());
		vo.setName(shareDir.getName());
		return vo;
	}

}
