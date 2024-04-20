package ltd.finelink.tool.disk.service.system;

import ltd.finelink.tool.disk.entity.system.EmailVerify;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jimmy
 * @since 2023-10-05
 */
public interface IEmailVerifyService extends IService<EmailVerify> {
	
	boolean verifySignupEmail(String email,String code);
	
	boolean verifyLoginEmail(String email,String code);
	
	boolean verifyResetEmail(String email,String code);

}
