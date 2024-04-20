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
@Table(name = "share_Dir", indexes = { @Index(name = "share_dir_uni_idx", columnList = "key", unique = true) })
@Data
public class ShareDir implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String key;

	private String path;  

	private Date shareTime;

}
