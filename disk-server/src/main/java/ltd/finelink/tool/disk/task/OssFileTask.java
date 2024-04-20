package ltd.finelink.tool.disk.task;

import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.finelink.tool.disk.config.NettyProperties;
import ltd.finelink.tool.disk.utils.DateUtil;

@Component
@Slf4j
@RequiredArgsConstructor
public class OssFileTask {
	
	private final NettyProperties nettyProperties;
	
	
	@Scheduled(cron = "0 30 0 * * ?")
	public void  delFile() {
		Configuration cfg = new Configuration(Region.region2());
		Auth auth = Auth.create(nettyProperties.getQiniuAk(), nettyProperties.getQiniuSk());
		BucketManager bucketManager = new BucketManager(auth, cfg);
		String prefix =  nettyProperties.getKeyPerfix()+DateUtil.format(DateUtil.addDay(new Date(), -5), "YYYYMMDD")+"/" ;
		BucketManager.FileListIterator fileListIterator = bucketManager.createFileListIterator(nettyProperties.getQiniuBucket(), prefix, 100, "");
		while (fileListIterator.hasNext()) {
		    //处理获取的file list结果
		    FileInfo[] items = fileListIterator.next();
		    for (FileInfo item : items) { 
		        try {
					bucketManager.delete(nettyProperties.getQiniuBucket(), item.key);
				} catch (QiniuException e) {
					 log.error("del error ",e);
				} 
		    }
		}
	}

}
