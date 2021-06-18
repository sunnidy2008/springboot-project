package com.battcn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.alibaba.fastjson.JSON;

import me.springboot.fwk.security.JwtUserFactory;
import me.springboot.fwk.sys.UserBO;

public class test {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
//		UserBO user = new UserBO();
////		user.setBirthday(new Date());
////		user.setDptId(UUID.randomUUID().toString());
////		user.setEmail("sunnidy2008@qq.com");
////		user.setId(UUID.randomUUID().toString());
////		user.setImg("http://www.baidu.com/"+UUID.randomUUID().toString());
////		user.setIssAt(new Date());
////		user.setNickName("绝世风舞雪");
////		user.setPassword(UUID.randomUUID().toString());
////		user.setPhone("18623658965");
////		List roles = new ArrayList();
////		roles.add("admin");
////		roles.add("superAdmin");
////		user.setRole(roles);
////		user.setUsername("admin");
//		JwtUserFactory factory = new JwtUserFactory();
//		String jString = JSON.toJSONString(user);
//		UserBO u = JSON.parseObject(jString, UserBO.class);
//		String token = factory.getJwtToken1(user);
//		
//		System.out.println(token);
		
		String a = "http://localhost:8085/pages/sys/menu/menu.html";
		String b = "/pages/sys/menu/menu.html";
		System.out.println(a.replace(b, ""));
	}

}
