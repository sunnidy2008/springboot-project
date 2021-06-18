package me.springboot.fwk.sys.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.springboot.fwk.sys.dao.SysAddressDao;
import me.springboot.fwk.sys.vo.SysAddressVO;
import java.util.Date;
import java.util.UUID;
import me.springboot.fwk.utils.VoUtils;
import me.springboot.fwk.sys.UserUtils;

import lombok.Getter;
import lombok.Setter;

import java.lang.String;

@Service
@Getter
@Setter
public class SysAddressService{

	@Autowired
	private SysAddressDao sysAddressDao;
	
	//按主键查找数据对象
	public SysAddressVO findByPrimaryKey(String primaryKey){
		return this.getSysAddressDao().getSysAddressJpa().findById(primaryKey).get();
	}
	
	//按主键删除对象
	public void deleteByPrimaryKey(String primaryKey){
		this.getSysAddressDao().getSysAddressJpa().deleteById(primaryKey);
	}
	
	//保存或更新对象
	public void saveOrUpdate(SysAddressVO bean){
		VoUtils.touch(bean,new Date(),UserUtils.getUser().getId());
		if(bean.getcCode()==null){
			bean.setcCode(UUID.randomUUID().toString());
		}
		this.getSysAddressDao().getSysAddressJpa().save(bean);
	}
}
