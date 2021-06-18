package me.springboot.fwk.websocket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.springboot.fwk.websocket.dao.SysWsMessageDao;
import me.springboot.fwk.websocket.vo.SysWsMessageVO;
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
public class SysWsMessageService{

	@Autowired
	private SysWsMessageDao sysWsMessageDao;
	
	//按主键查找数据对象
	public SysWsMessageVO findByPrimaryKey(String primaryKey){
		return this.getSysWsMessageDao().getSysWsMessageJpa().findById(primaryKey).get();
	}
	
	//按主键删除对象
	public void deleteByPrimaryKey(String primaryKey){
		this.getSysWsMessageDao().getSysWsMessageJpa().deleteById(primaryKey);
	}
	
	//保存或更新对象
	public void saveOrUpdate(SysWsMessageVO bean){
		VoUtils.touch(bean,new Date(),UserUtils.getUser().getId());
		if(bean.getcPkId()==null){
			bean.setcPkId(UUID.randomUUID().toString());
		}
		this.getSysWsMessageDao().getSysWsMessageJpa().save(bean);
	}
}
