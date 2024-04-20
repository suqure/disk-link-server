package ltd.finelink.tool.disk.client.service;

import java.util.List;

import ltd.finelink.tool.disk.client.entity.UserInfo;
import ltd.finelink.tool.disk.client.vo.IceServer;

public interface UserInfoService {

	UserInfo findByUsername(String username);

	UserInfo initUserInfo();
	
	UserInfo loginUser(String deviceId,String username,String password,boolean remember);
	
	UserInfo logout();

	void saveUser(UserInfo user);

	void updateUserCode(String username, String code);

	List<IceServer> getIceServer(String username, String token);

}
