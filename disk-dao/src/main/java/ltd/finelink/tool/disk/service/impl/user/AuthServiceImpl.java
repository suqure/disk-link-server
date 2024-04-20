package ltd.finelink.tool.disk.service.impl.user;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import ltd.finelink.tool.disk.entity.user.Auth;
import ltd.finelink.tool.disk.mapper.user.AuthMapper;
import ltd.finelink.tool.disk.service.user.IAuthService;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jimmy
 * @since 2023-08-11
 */
@Service
public class AuthServiceImpl extends ServiceImpl<AuthMapper, Auth> implements IAuthService {

	@Override
	public Auth findByTypeAndAuthName(int type, String authName) {
		QueryWrapper<Auth> query = new QueryWrapper<>();
		query.eq("type", type);
		query.eq("auth_name", authName);
		return baseMapper.selectOne(query);
	}

}
