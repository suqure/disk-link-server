package ltd.finelink.tool.disk.client.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ltd.finelink.tool.disk.client.dao.ShareDirRepository;
import ltd.finelink.tool.disk.client.entity.ShareDir;
import ltd.finelink.tool.disk.client.service.ShareDirService;

@Service
@RequiredArgsConstructor
public class ShareDirServiceImpl implements ShareDirService {

	private final ShareDirRepository shareDirRepository;

	@Override
	public void saveShareDirs(List<ShareDir> files) {
		shareDirRepository.saveAll(files);

	}

	@Override
	public void saveShareDir(ShareDir file) {
		shareDirRepository.save(file);

	}

	@Override
	public List<ShareDir> findAllShareDir() { 
		return shareDirRepository.findAll();
	}

	@Override
	public ShareDir findShareDirByKey(String key) {
		// TODO Auto-generated method stub
		return shareDirRepository.findByKey(key);
	}

	@Override
	public void deleteShareDir(Long id) {
		shareDirRepository.deleteById(id);

	}

}
