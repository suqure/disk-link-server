package ltd.finelink.tool.disk.entity.torrent;

import java.util.Date;

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
 * @since 2023-10-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value="torrent_meta_data",autoResultMap=true)
public class MetaData extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String hash;

    private byte[] data;
 

    private Long updateTime;


}
