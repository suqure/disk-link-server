package ltd.finelink.tool.disk.client.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.finelink.tool.disk.client.config.ClientProperties;
import ltd.finelink.tool.disk.client.utils.DeviceUtil;
import ltd.finelink.tool.disk.client.utils.OKHttpClientUtil;
import ltd.finelink.tool.disk.utils.MD5Util;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiService {

	public static final String DEVICE_PATH = "/device";

	public static final String ICE_SERVER_PATH = "/iceServer";
	
	public static final String LOGIN_PATH = "/login";

	private final ClientProperties properties;

	public JSONObject getDeviceInfo() {
		String url = properties.getApi() + DEVICE_PATH;
		try {
			Map<String, String> header = new HashMap<>();
			header.put("device-info", DeviceUtil.getDeviceInfo());
			String result = OKHttpClientUtil.get(url, header, null);
			return JSONObject.parseObject(result);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}

	public JSONObject getIceServers(String username, String token) {
		String url = properties.getApi() + ICE_SERVER_PATH;
		try {
			Map<String, String> header = new HashMap<>();
			header.put("username", username);
			header.put("token", token);
			String result = OKHttpClientUtil.get(url, header, null);
			return JSONObject.parseObject(result);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}
	
	public JSONObject login(String deviceId,String username, String password) {
		String url = properties.getApi() + LOGIN_PATH;
		try {
			Map<String, String> header = new HashMap<>();
			header.put("username", deviceId);
			JSONObject body = new JSONObject();
			body.put("type", 0);
			body.put("login", username);
			body.put("verify",MD5Util.encrypt(password));
			String result = OKHttpClientUtil.postJSON(url, header, body.toJSONString());
			return JSONObject.parseObject(result);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}

}
