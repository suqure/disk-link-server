package ltd.finelink.tool.disk.client.service;

import java.util.List;

import org.springframework.data.domain.Page;

import ltd.finelink.tool.disk.client.entity.UploadFile;

public interface UploadFileService {

	void saveUploadFiles(List<UploadFile> files);

	void saveUploadFile(UploadFile file);

	List<UploadFile> findUploadFileByDevice(String device, Integer status);

	Page<UploadFile> findUploadFilebyPage(int page, int pageSize);

	List<UploadFile> findAllUploadFile();

	UploadFile findFileByDeviceAndKey(String device, String key);

	void updateUploadFileStatus(String device, String key, Integer status);

	void deleteUploadFile(Long id);

}
