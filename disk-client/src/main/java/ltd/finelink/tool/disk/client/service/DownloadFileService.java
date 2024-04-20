package ltd.finelink.tool.disk.client.service;

import java.util.List;

import org.springframework.data.domain.Page;

import ltd.finelink.tool.disk.client.entity.DownloadFile;

public interface DownloadFileService {

	void saveDownloadFiles(List<DownloadFile> files);

	void saveDownloadFile(DownloadFile file);

	List<DownloadFile> findDownloadFileByDevice(String device, Integer status);

	Page<DownloadFile> findDowloadFilebyPage(int page, int pageSize);

	List<DownloadFile> findAllDownloadFile();

	DownloadFile findFileByKey(String key);

	void updateDownloadFileStatus(String key, Integer status);

	void deleteDownloadFile(Long id);

}
