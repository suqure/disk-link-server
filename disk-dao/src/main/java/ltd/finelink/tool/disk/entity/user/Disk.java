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
@TableName("user_disk")
public class Disk extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private Integer channel;

    private String deviceId;

    private String fileName;

    private String fullPath;

    private Long fileSize;

    private Boolean isDir; 

    private Long effectiveTime;


}
