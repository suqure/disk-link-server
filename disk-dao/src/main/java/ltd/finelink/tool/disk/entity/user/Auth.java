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
 * @since 2023-08-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_auth")
public class Auth extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 0用户名登录1手机登录2微信登录
     */
    private Integer type;

    private Long userId;

    private String authName;

    private String token; 

    private Long updateTime;


}
