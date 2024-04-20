package ltd.finelink.tool.disk.client.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ltd.finelink.tool.disk.client.dao.ShareFileRepository;
import ltd.finelink.tool.disk.client.entity.ShareFile;
import ltd.finelink.tool.disk.client.service.ShareFileService;

@Service
@RequiredArgsConstructor
public class ShareFileServiceImpl implements ShareFileService {

	private final ShareFileRepository shareFileRepository;

	@Override
	public void saveShareFiles(List<ShareFile> files) {

		shareFileRepository.saveAll(files);
	}

	@Override
	public void saveShareFile(ShareFile file) {

		shareFileRepository.save(file);

	}

	@Override
	public List<ShareFile> findAllShareFile() {

		return shareFileRepository.findAll();
	}

	@Override
	public ShareFile findShareFileByKey(String key) {

		return shareFileRepository.findByKey(key);
	}

	@Override
	public void deleteShareFile(Long id) {
		if(id!=null) {
			shareFileRepository.deleteById(id);
		} 
	}

}
