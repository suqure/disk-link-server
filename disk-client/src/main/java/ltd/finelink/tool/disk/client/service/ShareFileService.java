package ltd.finelink.tool.disk.client.service;


import java.util.List;

import ltd.finelink.tool.disk.client.entity.ShareFile;

public interface ShareFileService {
	
	void saveShareFiles(List<ShareFile> files);
	
	void saveShareFile(ShareFile file);
	
	List<ShareFile> findAllShareFile();
	
	ShareFile findShareFileByKey(String key);
	
	void deleteShareFile(Long id);

}
