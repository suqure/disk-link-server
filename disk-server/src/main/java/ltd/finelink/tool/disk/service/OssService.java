package ltd.finelink.tool.disk.service;

public interface OssService {

	String getQiniuWebToken(String ak, String sk, String bucket, String key, String notify);

	 
}
