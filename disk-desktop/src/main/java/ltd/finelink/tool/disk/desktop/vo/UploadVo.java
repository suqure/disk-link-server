package ltd.finelink.tool.disk.desktop.vo;

import java.util.Date;

import lombok.Data;
import ltd.finelink.tool.disk.client.entity.UploadFile;
import ltd.finelink.tool.disk.client.vo.FileInfo;

@Data
public class UploadVo {

	private Long id;

	private String name;

	private String key;

	private String device;

	private String path;

	private long size;

	private long total;

	private long chunks;

	private Integer status;

	private Date startTime;

	private Date finishTime;

	public FileInfo toFileInfo() {
		FileInfo file = new FileInfo();
		file.setChunks(chunks);
		file.setCurrent(chunks - 1);
		file.setTotal(total);
		file.setDevice(device);
		file.setId(key);
		file.setName(name);
		file.setSize(size);
		return file;
	}

	public static UploadVo createVo(UploadFile file) {
		UploadVo vo = new UploadVo();
		vo.setChunks(file.getChunks());
		vo.setDevice(file.getDevice());
		vo.setName(file.getName());
		vo.setTotal(file.getTotal());
		vo.setFinishTime(file.getFinishTime());
		vo.setStartTime(file.getStartTime());
		vo.setStatus(file.getStatus());
		vo.setSize(file.getSize());
		vo.setId(file.getId());
		vo.setKey(file.getKey());
		vo.setPath(file.getPath());
		return vo;
	}

}
