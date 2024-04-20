package ltd.finelink.tool.disk.service.impl.system;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import ltd.finelink.tool.disk.entity.system.EmailVerify;
import ltd.finelink.tool.disk.enums.EmailVerifyType;
import ltd.finelink.tool.disk.mapper.system.EmailVerifyMapper;
import ltd.finelink.tool.disk.service.system.IEmailVerifyService;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jimmy
 * @since 2023-10-05
 */
@Service
public class EmailVerifyServiceImpl extends ServiceImpl<EmailVerifyMapper, EmailVerify> implements IEmailVerifyService {

	@Override
	public boolean verifySignupEmail(String email, String code) {

		return verifyEmail(EmailVerifyType.REG, email, code);
	}

	@Override
	public boolean verifyLoginEmail(String email, String code) {

		return verifyEmail(EmailVerifyType.lOGIN, email, code);
	}

	private boolean verifyEmail(EmailVerifyType type, String email, String code) {
		QueryWrapper<EmailVerify> query = new QueryWrapper<>();
		query.eq("email", email);
		query.eq("type", type.getCode());
		query.eq("code", code);
		query.eq("used", false);
		query.ge("effective_time", System.currentTimeMillis());
		EmailVerify verify = baseMapper.selectOne(query);
		if (verify != null) {
			verify.setUsed(true);
			updateById(verify);
			return true;
		}
		return false;
	}

	@Override
	public boolean verifyResetEmail(String email, String code) {
		
		return verifyEmail(EmailVerifyType.RESET,email,code);
	}

}
