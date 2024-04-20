package ltd.finelink.tool.disk.client.service;


import java.util.List;

import ltd.finelink.tool.disk.client.entity.ShareDir;

public interface ShareDirService {
	
	void saveShareDirs(List<ShareDir> files);
	
	void saveShareDir(ShareDir file);
	
	List<ShareDir> findAllShareDir();
	
	ShareDir findShareDirByKey(String key);
	
	void deleteShareDir(Long id);

}
