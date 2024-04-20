package ltd.finelink.tool.disk.service.impl.system;

import ltd.finelink.tool.disk.entity.system.EmailVerify;
import ltd.finelink.tool.disk.entity.system.MessageTemplate;
import ltd.finelink.tool.disk.mapper.system.MessageTemplateMapper;
import ltd.finelink.tool.disk.service.system.IMessageTemplateService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jimmy
 * @since 2023-10-05
 */
@Service
public class MessageTemplateServiceImpl extends ServiceImpl<MessageTemplateMapper, MessageTemplate> implements IMessageTemplateService {

	@Override
	public MessageTemplate findByCode(String code) {
		QueryWrapper<MessageTemplate> query = new QueryWrapper<>(); 
		query.eq("code", code); 
		return baseMapper.selectOne(query);
	}

}
