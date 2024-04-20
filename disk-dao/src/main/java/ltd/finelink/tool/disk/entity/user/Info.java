package ltd.finelink.tool.disk.entity.user;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ltd.finelink.tool.disk.entity.BaseEntity;

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
@TableName("user_info")
public class Info extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 手机
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    private String registerIp;

    private Long registerTime;

    private Long updateTime;

    /**
     * 0无效1有效2禁用
     */
    private Integer status;


}
