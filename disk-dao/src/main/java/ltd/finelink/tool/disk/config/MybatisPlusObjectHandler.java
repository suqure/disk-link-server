package ltd.finelink.tool.disk.config;

import java.util.Date;

import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

import ltd.finelink.tool.disk.entity.BaseEntity;

@Component
public class MybatisPlusObjectHandler implements MetaObjectHandler {

	@Override
	public void insertFill(MetaObject metaObject) {
		setFieldValByName(BaseEntity.CREATETIME, System.currentTimeMillis(), metaObject);
	}

	@Override
	public void updateFill(MetaObject metaObject) {

	}

}
