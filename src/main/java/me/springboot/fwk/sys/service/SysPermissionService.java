package me.springboot.fwk.sys.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

import me.springboot.fwk.sys.UserUtils;
import me.springboot.fwk.sys.dao.SysPermissionDao;
import me.springboot.fwk.sys.vo.SysPermissionVO;
import me.springboot.fwk.utils.VoUtils;
import lombok.Getter;
import lombok.Setter;

import java.lang.String;

@Service
@Getter
@Setter
public class SysPermissionService{

	@Autowired
	private SysPermissionDao sysPermissionDao;
	
	//按主键查找数据对象
	public SysPermissionVO findByPrimaryKey(String primaryKey){
		return this.getSysPermissionDao().getSysPermissionJpa().findById(primaryKey).get();
	}
	
	//按主键删除对象
	public void deleteByPrimaryKey(String primaryKey){
		this.getSysPermissionDao().getSysPermissionJpa().deleteById(primaryKey);
	}
	
	//保存或更新对象
	public void saveOrUpdate(SysPermissionVO bean){
		VoUtils.touch(bean,new Date(),UserUtils.getUser().getId());
		if(bean.getcPkId()==null){
			bean.setcPkId(UUID.randomUUID().toString());
		}
		this.getSysPermissionDao().getSysPermissionJpa().save(bean);
	}
}
