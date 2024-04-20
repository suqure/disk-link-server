package ltd.finelink.tool.disk.service.torrent;

import ltd.finelink.tool.disk.entity.torrent.Node;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jimmy
 * @since 2023-10-20
 */
public interface INodeService extends IService<Node> {
	
	 Node findByNodeId(String nodeId);
	 
	 List<Node> findLastNode();

}
