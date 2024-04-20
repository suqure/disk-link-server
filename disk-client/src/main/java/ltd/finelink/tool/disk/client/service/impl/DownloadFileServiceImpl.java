package ltd.finelink.tool.disk.client.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ltd.finelink.tool.disk.client.dao.DownloadFileRepository;
import ltd.finelink.tool.disk.client.entity.DownloadFile;
import ltd.finelink.tool.disk.client.service.DownloadFileService;

@Service
@RequiredArgsConstructor
public class DownloadFileServiceImpl implements DownloadFileService {

	private final DownloadFileRepository downloadFileRepository;

	@Override
	public void saveDownloadFiles(List<DownloadFile> files) {

		downloadFileRepository.saveAll(files);
	}

	@Override
	public void saveDownloadFile(DownloadFile file) {
		downloadFileRepository.save(file);

	}

	@Override
	public Page<DownloadFile> findDowloadFilebyPage(int page, int pageSize) {

		return downloadFileRepository.findAll(PageRequest.of(page, pageSize));
	}

	@Override
	public void updateDownloadFileStatus(String key, Integer status) {

		downloadFileRepository.updateStatusByKey(status, key);
	}

	@Override
	public void deleteDownloadFile(Long id) {
		downloadFileRepository.deleteById(id);

	}

	@Override
	public List<DownloadFile> findAllDownloadFile() {

		return downloadFileRepository.findAll();
	}

	@Override
	public DownloadFile findFileByKey(String key) {
		return downloadFileRepository.findByKey(key);
	}

	@Override
	public List<DownloadFile> findDownloadFileByDevice(String device, Integer status) {

		return downloadFileRepository.findByDeviceAndStatus(device, status);
	}

}
