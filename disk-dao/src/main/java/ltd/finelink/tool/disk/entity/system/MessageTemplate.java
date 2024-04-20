package ltd.finelink.tool.disk.entity.system;

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
 * @since 2023-10-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MessageTemplate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String code;

    private String params;

    private String content; 

    private Long updateTime;


}
