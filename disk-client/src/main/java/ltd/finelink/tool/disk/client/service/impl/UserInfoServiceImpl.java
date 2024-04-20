package ltd.finelink.tool.disk.client.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import lombok.RequiredArgsConstructor;
import ltd.finelink.tool.disk.client.context.UserContext;
import ltd.finelink.tool.disk.client.dao.UserInfoRepository;
import ltd.finelink.tool.disk.client.entity.UserInfo;
import ltd.finelink.tool.disk.client.service.ApiService;
import ltd.finelink.tool.disk.client.service.UserInfoService;
import ltd.finelink.tool.disk.client.vo.IceServer;

@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

	private final UserInfoRepository userInfoRepository;

	private final ApiService apiService;
	  

	@Override
	public UserInfo findByUsername(String username) {
		return userInfoRepository.findByUsername(username);
	}

	@Override
	public UserInfo initUserInfo() {
		UserInfo user = userInfoRepository.findRemember();
		if(user==null) {
			user = userInfoRepository.findDefalut();
		} 
		if (user == null) {
			user = new UserInfo();
			JSONObject data = apiService.getDeviceInfo();
			if (data != null && data.getIntValue("code") == 200) {
				user.setChannel(data.getJSONObject("data").getString("channel"));
				user.setUsername(data.getJSONObject("data").getString("deviceId"));
				user.setDefalut(true);
				userInfoRepository.save(user);
			}
		}
		return user;
	}

	@Override
	public void saveUser(UserInfo user) {
		userInfoRepository.save(user);
	}

	@Override
	public void updateUserCode(String username, String code) {

		userInfoRepository.updateCodeByUsername(code, username);
	}

	@Override
	public List<IceServer> getIceServer(String username, String token) {
		JSONObject data = apiService.getIceServers(username, token);
		if (data != null && data.getIntValue("code") == 200) {
			JSONObject result = data.getJSONObject("data");
			return result.getJSONArray("iceServers").toJavaList(IceServer.class);
		}
		return null;
	}

	@Override
	public UserInfo loginUser(String deviceId,String username, String password,boolean remember) {
		JSONObject data = apiService.login(deviceId, username, password);
		if (data != null && data.getIntValue("code") == 200) {
			JSONObject result = data.getJSONObject("data");
			UserInfo login = result.toJavaObject(UserInfo.class);
			UserInfo user = UserContext.currentUser;
			login.setCode(user.getCode());
			login.setConfirmType(user.getConfirmType());
			login.setReadAuth(user.getReadAuth());
			login.setRequirePwd(user.isRequirePwd());
			login.setPassword(user.getPassword());
			UserContext.currentUser = login; 
			UserInfo record = findByUsername(username);
			if(record!=null) {
				login.setId(record.getId());
			}
			login.setRemember(remember);
			userInfoRepository.save(login); 
			return login;
		}
		return null;
	}

	@Override
	public UserInfo logout() {
		UserInfo user = UserContext.currentUser;
		if(user.isRemember()) {
			user.setRemember(false);
			userInfoRepository.save(user); 
		}
		UserInfo defalut = initUserInfo();
		defalut.setCode(user.getCode());
		defalut.setConfirmType(user.getConfirmType());
		defalut.setReadAuth(user.getReadAuth());
		defalut.setRequirePwd(user.isRequirePwd());
		defalut.setPassword(user.getPassword());
		UserContext.currentUser = defalut; 
		userInfoRepository.save(defalut); 
		return defalut;
	}
	
	

}
