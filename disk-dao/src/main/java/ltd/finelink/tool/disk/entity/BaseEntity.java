package ltd.finelink.tool.disk.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;

import lombok.Data;

/**
 * @Description 基类bean
 * @author jimmy
 * @date 2021年12月2日
 */
@Data
public class BaseEntity implements Serializable {

	/**
	 * 主键Id
	 */
	@TableId
	private Long id;

	/**
	 * 创建人时间
	 */
	@TableField(fill = FieldFill.INSERT)
	private Long createTime;

	public static final String ID = "id";

	public static final String CREATETIME = "createTime";
}
