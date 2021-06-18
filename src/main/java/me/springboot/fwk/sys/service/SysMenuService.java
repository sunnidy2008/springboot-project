package me.springboot.fwk.sys.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

import me.springboot.fwk.sys.UserUtils;
import me.springboot.fwk.sys.dao.SysMenuDao;
import me.springboot.fwk.sys.vo.SysMenuVO;
import me.springboot.fwk.utils.VoUtils;
import lombok.Getter;
import lombok.Setter;

import java.lang.String;

@Service
@Getter
@Setter
public class SysMenuService{

	@Autowired
	private SysMenuDao sysMenuDao;
	
	//按主键查找数据对象
	public SysMenuVO findByPrimaryKey(String primaryKey){
		return this.getSysMenuDao().getSysMenuJpa().findById(primaryKey).get();
	}
	
	//按主键删除对象
	public void deleteByPrimaryKey(String primaryKey){
		this.getSysMenuDao().getSysMenuJpa().deleteById(primaryKey);
	}
	
	//保存或更新对象
	public void saveOrUpdate(SysMenuVO bean){
		VoUtils.touch(bean,new Date(),UserUtils.getUser().getId());
		if(bean.getcMenuCode()==null){
			bean.setcMenuCode(UUID.randomUUID().toString());
		}
		this.getSysMenuDao().getSysMenuJpa().save(bean);
	}
}
