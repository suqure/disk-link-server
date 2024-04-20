package ltd.finelink.tool.disk.client.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ltd.finelink.tool.disk.client.entity.UploadFile;

@Repository
public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {

	@Query("select f from UploadFile f where f.id <= ?1")
    Page<UploadFile> findMore(Long maxId, Pageable pageable);
	
	@Query("select f from UploadFile f where f.name like ?1||'%'")
    Page<UploadFile> findByName(String name, Pageable pageable);
	
	@Query("select f from UploadFile f where f.device = ?1 and f.key=?2")
	UploadFile findByDeviceAndKey(String device,String key);
	 
	@Query("select f from UploadFile f where f.device = ?1 and f.status=?2")
    List<UploadFile> findByDeviceAndStatus(String device, Integer status);
	 
	@Transactional
	@Modifying 
    @Query("update UploadFile f set f.status = ?1 where f.device = ?2 and f.key = ?3")
    int updateStatusByDeviceAndKey(Integer status,String device, String key);
	
	@Transactional
	@Modifying 
    @Query("update UploadFile f set f.chunks = ?1 where f.device = ?2 and f.key = ?3")
    int updateChunksByDeviceAndKey(long chunks, String device,String key);
}
