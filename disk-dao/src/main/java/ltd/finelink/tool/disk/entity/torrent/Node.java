package ltd.finelink.tool.disk.entity.torrent;

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
 * @since 2023-10-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("torrent_node")
public class Node extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String nodeId;

    private String ip;

    private Integer port; 

    private Long updateTime;


}
