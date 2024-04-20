package ltd.finelink.tool.disk.client.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "user_info", indexes = { @Index(name = "username_uni_idx", columnList = "username", unique = true) })
@Data
public class UserInfo implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String username;

	private String channel;

	private String code;
	
	private String nickname;
	
	private String email;
	
	private String avatar;

	private String token;

	private String path;

	private String writeAuth;

	private String readAuth;

	private boolean requirePwd;

	private String password;

	private int confirmType;

	private boolean defalut;
	
	private boolean remember;

}
