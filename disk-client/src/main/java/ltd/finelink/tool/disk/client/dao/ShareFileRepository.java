package ltd.finelink.tool.disk.client.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ltd.finelink.tool.disk.client.entity.ShareFile;

@Repository
public interface ShareFileRepository extends JpaRepository<ShareFile, Long> {

	@Query("select f from ShareFile f where f.id <= ?1")
    Page<ShareFile> findMore(Long maxId, Pageable pageable);
	
	@Query("select f from ShareFile f where f.name like ?1||'%'")
    Page<ShareFile> findByName(String name, Pageable pageable);
	
	ShareFile findByKey(String key);
}
