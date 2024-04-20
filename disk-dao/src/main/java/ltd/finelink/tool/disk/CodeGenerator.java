package ltd.finelink.tool.disk;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

public class CodeGenerator {
	/**
	 * <p>
	 * 读取控制台内容
	 * </p>
	 */
	public static String scanner(String tip) {
		Scanner scanner = new Scanner(System.in);
		StringBuilder help = new StringBuilder();
		help.append("请输入" + tip + "：");
		System.out.println(help.toString());
		if (scanner.hasNext()) {
			String ipt = scanner.next();
			if (StringUtils.isNotBlank(ipt)) {
				return ipt;
			}
		}
		throw new MybatisPlusException("请输入正确的" + tip + "！");
	}

	public static void main(String[] args) {
		String basePackage = "ltd.finelink.tool.disk";
		//数据库配置
		String diverName = "com.mysql.jdbc.Driver";
		String jdbc_url = "jdbc:mysql://192.168.56.104:3306/link_disk?useUnicode=true&useSSL=false&characterEncoding=utf8";
		String username = "root";
		String password = "123456";
		// 代码生成器
		AutoGenerator mpg = new AutoGenerator();

		// 全局配置
		GlobalConfig gc = new GlobalConfig();
		String projectPath = System.getProperty("user.dir");
		gc.setOutputDir(projectPath + "/src/main/java");
		gc.setAuthor("jimmy");
		gc.setOpen(false);
		gc.setDateType(DateType.ONLY_DATE); 
		// gc.setSwagger2(true); 实体属性 Swagger2 注解
		mpg.setGlobalConfig(gc);

		// 数据源配置
		DataSourceConfig dsc = new DataSourceConfig();
		dsc.setUrl(jdbc_url);
		// dsc.setSchemaName("public");
		dsc.setDriverName(diverName);
		dsc.setUsername(username);
		dsc.setPassword(password);
		mpg.setDataSource(dsc);

		// 包配置
		PackageConfig pc = new PackageConfig();
		pc.setModuleName(scanner("模块名"));
		pc.setParent(basePackage);
		 
		//自定义包名
		Map<String,String>  packageInfo = CollectionUtils.newHashMapWithExpectedSize(7);
		Map<String,String> pathInfo = CollectionUtils.newHashMapWithExpectedSize(7);
		packageInfo.put(ConstVal.MODULE_NAME, pc.getModuleName());
		packageInfo.put(ConstVal.ENTITY, basePackage+StringPool.DOT+pc.getEntity()+StringPool.DOT+pc.getModuleName());
		packageInfo.put(ConstVal.MAPPER, basePackage+StringPool.DOT+pc.getMapper()+StringPool.DOT+pc.getModuleName() );

		packageInfo.put(ConstVal.SERVICE,basePackage+StringPool.DOT+pc.getService()+StringPool.DOT+pc.getModuleName());
		packageInfo.put(ConstVal.SERVICE_IMPL,basePackage+StringPool.DOT+pc.getServiceImpl()+StringPool.DOT+pc.getModuleName());
		packageInfo.put(ConstVal.CONTROLLER, basePackage+StringPool.DOT+pc.getController()+StringPool.DOT+pc.getModuleName());
		 
		pathInfo.put(ConstVal.ENTITY_PATH, joinPath(gc.getOutputDir(),packageInfo.get(ConstVal.ENTITY)));
		pathInfo.put(ConstVal.MAPPER_PATH, joinPath(gc.getOutputDir(),packageInfo.get(ConstVal.MAPPER)));
		pathInfo.put(ConstVal.SERVICE_PATH, joinPath(gc.getOutputDir(),packageInfo.get(ConstVal.SERVICE)));
		pathInfo.put(ConstVal.SERVICE_IMPL_PATH, joinPath(gc.getOutputDir(),packageInfo.get(ConstVal.SERVICE_IMPL)));
		pathInfo.put(ConstVal.CONTROLLER_PATH, joinPath(gc.getOutputDir(),packageInfo.get(ConstVal.CONTROLLER)));
		pc.setPathInfo(pathInfo);
		mpg.setPackageInfo(pc);

		// 自定义配置
		InjectionConfig cfg = new InjectionConfig() {
			@Override
			public void initMap() {
				// to do nothing
			}
		};

		// 如果模板引擎是 freemarker
		String templatePath = "/templates/mapper.xml.ftl";
		// 如果模板引擎是 velocity
		// String templatePath = "/templates/mapper.xml.vm";

		// 自定义输出配置
		List<FileOutConfig> focList = new ArrayList<>();
		// 自定义配置会被优先输出
		focList.add(new FileOutConfig(templatePath) {
			@Override
			public String outputFile(TableInfo tableInfo) {
				// 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
				return projectPath + "/src/main/resources/mapper/" + pc.getModuleName() + "/"
						+ tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
			}
		});
 
		  
		cfg.setFileOutConfigList(focList);
		mpg.setCfg(cfg);
		
		

		// 配置模板
		TemplateConfig templateConfig = new TemplateConfig();
 

		templateConfig.setXml(null);
		mpg.setTemplate(templateConfig);

		// 策略配置
		StrategyConfig strategy = new StrategyConfig();
		strategy.setNaming(NamingStrategy.underline_to_camel);
		strategy.setColumnNaming(NamingStrategy.underline_to_camel);
		strategy.setSuperEntityClass("ltd.finelink.tool.disk.entity.BaseEntity");
		strategy.setEntityLombokModel(true);
		strategy.setRestControllerStyle(true);
		// 公共父类
//		strategy.setSuperControllerClass("你自己的父类控制器,没有就不用设置!");
		// 写于父类中的公共字段
		strategy.setSuperEntityColumns("id");
		strategy.setInclude(scanner("表名，多个英文逗号分割").split(","));
		strategy.setControllerMappingHyphenStyle(true);
		strategy.setTablePrefix(pc.getModuleName() + "_");
		mpg.setStrategy(strategy);
		mpg.setTemplateEngine(new FreemarkerTemplateEngine());
		ConfigBuilder builder = new ConfigBuilder(mpg.getPackageInfo(), mpg.getDataSource(), strategy, mpg.getTemplate(), mpg.getGlobalConfig());
		try {
			Field field = builder.getClass().getDeclaredField("packageInfo");
			field.setAccessible(true);
			field.set(builder, packageInfo);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mpg.setConfig(builder);
		mpg.execute();
	}
	private static String joinPackage(String parent, String subPackage) {
        return StringUtils.isBlank(parent) ? subPackage : (parent + StringPool.DOT + subPackage);
    }

	 private static String joinPath(String parentDir, String packageName) {
	        if (StringUtils.isBlank(parentDir)) {
	            parentDir = System.getProperty(ConstVal.JAVA_TMPDIR);
	        }
	        if (!StringUtils.endsWith(parentDir, File.separator)) {
	            parentDir += File.separator;
	        }
	        packageName = packageName.replaceAll("\\.", StringPool.BACK_SLASH + File.separator);
	        return parentDir + packageName;
	    }
}
