package ltd.finelink.tool.disk.service.impl.user;

import ltd.finelink.tool.disk.entity.user.AnonymousInfo;
import ltd.finelink.tool.disk.mapper.user.AnonymousInfoMapper;
import ltd.finelink.tool.disk.service.user.IAnonymousInfoService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jimmy
 * @since 2023-08-11
 */
@Service
public class AnonymousInfoServiceImpl extends ServiceImpl<AnonymousInfoMapper, AnonymousInfo> implements IAnonymousInfoService {

	@Override
	public AnonymousInfo findByDeviceId(String deviceId) {
		QueryWrapper<AnonymousInfo> query = new QueryWrapper<>();
		query.eq("device_id", deviceId); 
		return baseMapper.selectOne(query);
	}

	@Override
	public boolean existByDeviceId(String deviceId) {
		QueryWrapper<AnonymousInfo> query = new QueryWrapper<>();
		query.eq("device_id", deviceId); 
		Integer count = baseMapper.selectCount(query); 
		if(count==null||count==0) {
			return false;
		}
		return true;
	}

}
