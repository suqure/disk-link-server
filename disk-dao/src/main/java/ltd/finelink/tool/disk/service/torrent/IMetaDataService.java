package ltd.finelink.tool.disk.service.torrent;

import com.baomidou.mybatisplus.extension.service.IService;

import ltd.finelink.tool.disk.entity.torrent.MetaData;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jimmy
 * @since 2023-10-21
 */
public interface IMetaDataService extends IService<MetaData> {
	
	MetaData findByHash(String hash);

}
