package ltd.finelink.tool.disk.service.torrent;

import ltd.finelink.tool.disk.entity.torrent.Hash;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jimmy
 * @since 2023-10-20
 */
public interface IHashService extends IService<Hash> {
	
	Hash findByInfoHash(String infoHash);

}
