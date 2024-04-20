package ltd.finelink.tool.disk.entity.user;

import com.baomidou.mybatisplus.annotation.TableName;
import ltd.finelink.tool.disk.entity.BaseEntity;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author jimmy
 * @since 2023-10-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_comment")
public class Comment extends BaseEntity {

    private static final long serialVersionUID = 1L;
    
    private Integer type;

    private String code;

    private String deviceId;

    private String comment;

    private Long createTime;


}
