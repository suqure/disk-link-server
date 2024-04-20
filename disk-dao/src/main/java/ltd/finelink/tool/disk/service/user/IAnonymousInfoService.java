package ltd.finelink.tool.disk.service.user;

import ltd.finelink.tool.disk.entity.user.AnonymousInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jimmy
 * @since 2023-08-11
 */
public interface IAnonymousInfoService extends IService<AnonymousInfo> {
	
	
	AnonymousInfo findByDeviceId(String deviceId);
	
	boolean existByDeviceId(String deviceId);

}
