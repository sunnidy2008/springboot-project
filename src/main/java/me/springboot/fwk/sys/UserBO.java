package me.springboot.fwk.sys;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class UserBO {

	private String id;
	private String username;
	private String password;
	private String phone;
	private String email;
	private Date birthday;
	private String nickName;
	private String img;
	private String sex;
	private String dptId;
	private List<String> role=new ArrayList<String>();
	
	private Date issAt=new Date();//生成该对象的时间，用于与数据库比较密码的最后变更时间是否在此时间之后，若在之后，说明该对象已过期
}
