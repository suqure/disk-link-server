package ltd.finelink.tool.disk.desktop.vo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Data;
import ltd.finelink.tool.disk.client.entity.ShareFile;

@Data
public class ShareFileVo {
	
	private long id;
	
	private String name;

	private String key;

	private String path;

	private long size;
	
	private String format;
	
	private BooleanProperty checked = new SimpleBooleanProperty(false);
	
	public static ShareFileVo createShareFileVo(ShareFile shareFile) {
		ShareFileVo vo = new ShareFileVo();
		vo.setFormat(shareFile.getFormat());
		vo.setId(shareFile.getId());
		vo.setPath(shareFile.getPath());
		vo.setKey(shareFile.getKey());
		vo.setName(shareFile.getName());
		vo.setSize(shareFile.getSize());
		return vo;
	}

}
