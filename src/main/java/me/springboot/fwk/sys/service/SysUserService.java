package me.springboot.fwk.sys.service;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;
import me.springboot.fwk.sys.UserUtils;
import me.springboot.fwk.sys.dao.SysUserDao;
import me.springboot.fwk.sys.vo.SysUserVO;
import me.springboot.fwk.utils.VoUtils;

@Service
@Getter
@Setter
public class SysUserService {
	@Autowired
	private SysUserDao sysUserDao;
	
	//按主键查找数据对象
	public SysUserVO findByPrimaryKey(String primaryKey){
		return this.getSysUserDao().getSysUserJpa().findById(primaryKey).get();
	}
	
	//按主键删除对象
	public void deleteByPrimaryKey(String primaryKey){
		this.getSysUserDao().getSysUserJpa().deleteById(primaryKey);
	}
	
	//保存或更新对象
	public void saveOrUpdate(SysUserVO bean){
		VoUtils.touch(bean,new Date(),UserUtils.getUser().getId());
		if(bean.getcPkId()==null){
			bean.setcPkId(UUID.randomUUID().toString());
		}
		this.getSysUserDao().getSysUserJpa().save(bean);
	}
}
