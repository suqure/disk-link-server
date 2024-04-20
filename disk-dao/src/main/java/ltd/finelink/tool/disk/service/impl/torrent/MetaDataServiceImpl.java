package ltd.finelink.tool.disk.service.impl.torrent;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import ltd.finelink.tool.disk.entity.torrent.MetaData;
import ltd.finelink.tool.disk.mapper.torrent.MetaDataMapper;
import ltd.finelink.tool.disk.service.torrent.IMetaDataService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jimmy
 * @since 2023-10-21
 */
@Service
public class MetaDataServiceImpl extends ServiceImpl<MetaDataMapper, MetaData> implements IMetaDataService {

	@Override
	public MetaData findByHash(String hash) {
		QueryWrapper<MetaData> query = new QueryWrapper<>();
		query.eq("hash", hash);
		return this.baseMapper.selectOne(query);
	}

}
