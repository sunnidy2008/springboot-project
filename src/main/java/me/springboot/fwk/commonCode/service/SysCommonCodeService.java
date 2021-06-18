package me.springboot.fwk.commonCode.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.springboot.fwk.commonCode.dao.SysCommonCodeDao;
import me.springboot.fwk.commonCode.vo.SysCommonCodeVO;
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
public class SysCommonCodeService{

	@Autowired
	private SysCommonCodeDao sysCommonCodeDao;
	
	//按主键查找数据对象
	public SysCommonCodeVO findByPrimaryKey(String primaryKey){
		return this.getSysCommonCodeDao().getSysCommonCodeJpa().findById(primaryKey).get();
	}
	
	//按主键删除对象
	public void deleteByPrimaryKey(String primaryKey){
		this.getSysCommonCodeDao().getSysCommonCodeJpa().deleteById(primaryKey);
	}
	
	//保存或更新对象
	public void saveOrUpdate(SysCommonCodeVO bean){
		VoUtils.touch(bean,new Date(),UserUtils.getUser().getId());
		if(bean.getcPkId()==null){
			bean.setcPkId(UUID.randomUUID().toString());
		}
		
		this.getSysCommonCodeDao().getSysCommonCodeJpa().save(bean);
	}
}
