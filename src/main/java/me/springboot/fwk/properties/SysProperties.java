package me.springboot.fwk.properties;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
//@PropertySource("classpath:SysProperties.yml")
//@ConfigurationProperties(prefix = "sys")
@ConfigurationProperties
public class SysProperties {

//	@Value("${sys.agreement}")
//	private String agreement;
	
	@Value("${sys.intercept.url.enable}")
	private boolean enableUrlIntercept=false;
	
	@Value("${sys.intercept.url.excludeRole}")
	private List<Object> excludeUrlInterceptRole=new ArrayList<Object>();
	
	@Value("${sys.intercept.role.enable}")
	private boolean enableRoleIntercept=false;
	
	@Value("${sys.intercept.role.excludeRole}")
	private List<Object> excludeRoleInterceptRole=new ArrayList<Object>();
	
	
	@Value("${sys.sso.url}")
	private String sso_url;
	
	@Value("${sys.server.id}")
	private String server_id;
	
	@Value("${sys.server.url}")
	private String server_url;
	
	@Value("${sys.ignorePath}")
	private String ignore_path;
	
	@Value("${sys.path.files}")
	private String filesPath;
	
	@Value("${sys.path.upload}")
	private String uploadPath;
	
	@Value("${sys.path.download}")
	private String downloadPath;
	

//	// 加载YML格式自定义配置文件
//	@Bean
//	public static PropertySourcesPlaceholderConfigurer properties() {
//		PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
//		YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
////		yaml.setResources(new FileSystemResource("SysProperties.yml"));//File引入
//		yaml.setResources(new ClassPathResource("CustomYml.yml"));//class引入
//		configurer.setProperties(yaml.getObject());
//		return configurer;
//	}
}
