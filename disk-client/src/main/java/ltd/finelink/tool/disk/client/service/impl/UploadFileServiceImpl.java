package ltd.finelink.tool.disk.client.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ltd.finelink.tool.disk.client.dao.UploadFileRepository;
import ltd.finelink.tool.disk.client.entity.UploadFile;
import ltd.finelink.tool.disk.client.service.UploadFileService;

@Service
@RequiredArgsConstructor
public class UploadFileServiceImpl implements UploadFileService {

	private final UploadFileRepository uploadFileRepository;

	@Override
	public void saveUploadFiles(List<UploadFile> files) {
		uploadFileRepository.saveAll(files);
	}

	@Override
	public void saveUploadFile(UploadFile file) {

		uploadFileRepository.save(file);
	}

	@Override
	public List<UploadFile> findUploadFileByDevice(String device, Integer status) {

		return uploadFileRepository.findByDeviceAndStatus(device, status);
	}

	@Override
	public Page<UploadFile> findUploadFilebyPage(int page, int pageSize) {

		return uploadFileRepository.findAll(PageRequest.of(page, pageSize));
	}

	@Override
	public List<UploadFile> findAllUploadFile() {
		 
		return uploadFileRepository.findAll();
	}

	@Override
	public UploadFile findFileByDeviceAndKey(String device, String key) {
		// TODO Auto-generated method stub
		return uploadFileRepository.findByDeviceAndKey(device, key);
	}

	@Override
	public void updateUploadFileStatus(String device,String key, Integer status) {

		uploadFileRepository.updateStatusByDeviceAndKey(status, device, key);
	}

	@Override
	public void deleteUploadFile(Long id) {
		uploadFileRepository.deleteById(id);

	}

}
