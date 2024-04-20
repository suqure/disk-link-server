package ltd.finelink.tool.disk.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.finelink.tool.disk.service.OssService;
@Slf4j
@Service
@RequiredArgsConstructor
public class OssServiceImpl implements OssService {

	@Override
	public String getQiniuWebToken(String ak, String sk, String bucket,String key,String notify) {
		Auth auth = Auth.create(ak, sk);
		if(StringUtils.isNotBlank(notify)) {
			StringMap putPolicy = new StringMap();
			putPolicy.put("callbackUrl",notify);
			putPolicy.put("callbackBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"fsize\":$(fsize)}");
			putPolicy.put("callbackBodyType", "application/json");
			return auth.uploadToken(bucket, key, 3600, putPolicy);
		}
		
		return auth.uploadToken(bucket,key);
	}

}
