package ltd.finelink.tool.disk.client.dao;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ltd.finelink.tool.disk.client.entity.UserInfo;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

	UserInfo findByUsername(String username);
	@Transactional
	@Modifying 
    @Query("update UserInfo u set u.code = ?1 where u.username = ?2")
    int updateCodeByUsername(String code, String username);
	
	@Query("select u from UserInfo u where u.defalut= true")
	UserInfo findDefalut();
	
	@Query("select u from UserInfo u where u.remember= true")
	UserInfo findRemember();

}
