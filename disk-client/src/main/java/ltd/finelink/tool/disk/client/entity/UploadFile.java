package ltd.finelink.tool.disk.client.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "upload_file", indexes = { @Index(name = "u_file_idx", columnList = "device", unique = true),
		@Index(name = "u_file_idx", columnList = "key", unique = true) })
@Data
public class UploadFile implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String key;

	private String device;

	private String path;

	private int chunkSize;

	private int type;

	private long size;

	private long total;

	private long chunks;

	private Integer status;

	private Date startTime;

	private Date finishTime;

}
