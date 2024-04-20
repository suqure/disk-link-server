package ltd.finelink.tool.disk.entity.user;

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
public class AnonymousInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String deviceId;
    
    private String deviceInfo;

    private String userAgent; 

    private String ip;

    private Long lastLoginTime;


}
