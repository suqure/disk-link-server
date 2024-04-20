package ltd.finelink.tool.disk.service.impl.user;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import ltd.finelink.tool.disk.entity.user.Info;
import ltd.finelink.tool.disk.mapper.user.InfoMapper;
import ltd.finelink.tool.disk.service.user.IInfoService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jimmy
 * @since 2023-08-11
 */
@Service
public class InfoServiceImpl extends ServiceImpl<InfoMapper, Info> implements IInfoService {

	@Override
	public boolean checkEmailExist(String email) {
		QueryWrapper<Info> query = new QueryWrapper<>(); 
		query.eq("email", email);
		Integer count = baseMapper.selectCount(query); 
		if(count==null||count==0) {
			return false;
		}
		return true;
	}

	@Override
	public boolean checkUsernameExist(String username) { 
		QueryWrapper<Info> query = new QueryWrapper<>(); 
		query.eq("user_name", username);
		Integer count = baseMapper.selectCount(query); 
		if(count==null||count==0) {
			return false;
		}
		return true;
	}

	@Override
	public Info findByUsername(String username) {
		QueryWrapper<Info> query = new QueryWrapper<>(); 
		query.eq("user_name", username); 
		return baseMapper.selectOne(query);
	}

	@Override
	public Info findByEmail(String email) {
		QueryWrapper<Info> query = new QueryWrapper<>(); 
		query.eq("email", email); 
		return baseMapper.selectOne(query);
	}

}
