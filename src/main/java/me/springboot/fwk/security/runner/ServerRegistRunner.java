package me.springboot.fwk.security.runner;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import me.springboot.fwk.properties.SysProperties;
import me.springboot.fwk.redis.RedisUtils;

@Component      //被 spring 容器管理
@Order(2)     //如果多个自定义的 ApplicationRunner  ，用来标明执行的顺序
public class ServerRegistRunner implements ApplicationRunner{

	@Autowired
	private RedisUtils redisUtils;
	@Autowired
	private SysProperties sysProperties;
	
	//服务器启动后注册应用服务器
	@Override
	public void run(ApplicationArguments args) throws Exception {
		// TODO Auto-generated method stub
		String server_id = sysProperties.getServer_id();
		String server_url = sysProperties.getServer_url();
		if(!StringUtils.isBlank(server_id) && !StringUtils.isBlank(server_url)){
			redisUtils.set(server_id, server_url);
		}
	}

}
