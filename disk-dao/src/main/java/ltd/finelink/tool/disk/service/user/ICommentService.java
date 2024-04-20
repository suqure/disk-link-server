package ltd.finelink.tool.disk.service.user;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import ltd.finelink.tool.disk.entity.user.Comment;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jimmy
 * @since 2023-10-10
 */
public interface ICommentService extends IService<Comment> {
	
	List<Comment> findLastComment(Long lastId,Integer rows);

}
