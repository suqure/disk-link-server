package ltd.finelink.tool.disk.service.impl.user;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import ltd.finelink.tool.disk.entity.user.Comment;
import ltd.finelink.tool.disk.mapper.user.CommentMapper;
import ltd.finelink.tool.disk.service.user.ICommentService;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jimmy
 * @since 2023-10-10
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

	@Override
	public List<Comment> findLastComment(Long lastId, Integer rows) {
		QueryWrapper<Comment> query = new QueryWrapper<>();
		if (lastId != null && lastId > 0) {
			query.lt("id", lastId);
		}

		if (rows == null || rows >20) {
			query.last("limit 20");
		} else {
			query.last("limit " + rows);
		}
		query.orderByDesc("id");
		return this.baseMapper.selectList(query);
	}

}
