package ltd.finelink.tool.disk.service.impl.torrent;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import ltd.finelink.tool.disk.entity.torrent.Node;
import ltd.finelink.tool.disk.mapper.torrent.NodeMapper;
import ltd.finelink.tool.disk.service.torrent.INodeService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jimmy
 * @since 2023-10-20
 */
@Service
public class NodeServiceImpl extends ServiceImpl<NodeMapper, Node> implements INodeService {

	@Override
	public Node findByNodeId(String nodeId) {
		 
		QueryWrapper<Node> query = new QueryWrapper<>();
		query.eq("node_id", nodeId);
		return this.baseMapper.selectOne(query);
	}

	@Override
	public List<Node> findLastNode() {
		QueryWrapper<Node> query = new QueryWrapper<>();
		query.orderByDesc("update_time");
		query.last("limit 20");
		return this.baseMapper.selectList(query);
	}

}
