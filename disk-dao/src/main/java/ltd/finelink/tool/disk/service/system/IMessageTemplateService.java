package ltd.finelink.tool.disk.service.system;

import ltd.finelink.tool.disk.entity.system.MessageTemplate;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jimmy
 * @since 2023-10-05
 */
public interface IMessageTemplateService extends IService<MessageTemplate> {
	
	
	MessageTemplate findByCode(String code);
}
