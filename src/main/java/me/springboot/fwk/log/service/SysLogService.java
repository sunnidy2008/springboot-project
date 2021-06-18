package me.springboot.fwk.log.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.springboot.fwk.log.dao.SysLogDao;
import me.springboot.fwk.log.vo.SysLogVO;
import me.springboot.fwk.sys.UserUtils;

import java.util.Date;
import java.util.UUID;
import me.springboot.fwk.utils.VoUtils;
import lombok.Getter;
import lombok.Setter;

import java.lang.String;

@Service
@Getter
@Setter
public class SysLogService{

	@Autowired
	private SysLogDao sysLogDao;
	
	//按主键查找数据对象
	public SysLogVO findByPrimaryKey(String primaryKey){
		return this.getSysLogDao().getSysLogJpa().findById(primaryKey).get();
	}
	
	//按主键删除对象
	public void deleteByPrimaryKey(String primaryKey){
		this.getSysLogDao().getSysLogJpa().deleteById(primaryKey);
	}
	
	//保存或更新对象
	public void saveOrUpdate(SysLogVO bean){
		VoUtils.touch(bean,new Date(),UserUtils.getUser().getId());
		if(bean.getcPkId()==null){
			bean.setcPkId(UUID.randomUUID().toString());
		}
		this.getSysLogDao().getSysLogJpa().save(bean);
	}
}
