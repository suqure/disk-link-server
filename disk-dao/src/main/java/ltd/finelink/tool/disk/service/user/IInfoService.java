package ltd.finelink.tool.disk.service.user;

import com.baomidou.mybatisplus.extension.service.IService;

import ltd.finelink.tool.disk.entity.user.Info;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jimmy
 * @since 2023-08-11
 */
public interface IInfoService extends IService<Info> {
	
	boolean checkEmailExist(String email);
	
	boolean checkUsernameExist(String username);
	
	Info findByUsername(String username);
	
	Info findByEmail(String email);

}
