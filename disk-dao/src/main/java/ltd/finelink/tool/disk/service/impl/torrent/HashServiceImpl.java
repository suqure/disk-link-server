package ltd.finelink.tool.disk.service.impl.torrent;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import ltd.finelink.tool.disk.entity.torrent.Hash;
import ltd.finelink.tool.disk.mapper.torrent.HashMapper;
import ltd.finelink.tool.disk.service.torrent.IHashService;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jimmy
 * @since 2023-10-20
 */
@Service
public class HashServiceImpl extends ServiceImpl<HashMapper, Hash> implements IHashService {

	@Override
	public Hash findByInfoHash(String infoHash) {
		QueryWrapper<Hash> query = new QueryWrapper<>();
		query.eq("info_hash", infoHash);
		return this.baseMapper.selectOne(query);
	}

}
