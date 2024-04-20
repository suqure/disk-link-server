package ltd.finelink.tool.disk.client.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ltd.finelink.tool.disk.client.entity.DownloadFile;

@Repository
public interface DownloadFileRepository extends JpaRepository<DownloadFile, Long> {

	@Query("select f from DownloadFile f where f.id <= ?1")
    Page<DownloadFile> findMore(Long maxId, Pageable pageable);
	
	@Query("select f from DownloadFile f where f.name like ?1||'%'")
    Page<DownloadFile> findByName(String name, Pageable pageable);
	
	DownloadFile findByKey(String key);
	
	
	@Query("select f from DownloadFile f where f.device = ?1 and f.status=?2")
    List<DownloadFile> findByDeviceAndStatus(String device, Integer status);
	
	
	@Transactional
	@Modifying 
    @Query("update DownloadFile f set f.status = ?1 where f.key = ?2")
    int updateStatusByKey(Integer status, String key);
	
	@Transactional
	@Modifying 
    @Query("update DownloadFile f set f.chunks = ?1 where f.key = ?2")
    int updateChunksByKey(long chunks, String key);
}
