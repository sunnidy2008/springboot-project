package me.springboot.biz.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.springboot.fwk.sys.dao.SysRoleDao;

@Service
public class OptionService {

	@Autowired
	private SysRoleDao sysRoleDao;
	/**
	 * @param key 需要调用的service的标识
	 * @return
	 */
	public List<Map<String,String>> getOptions(String key){
		List<Map<String,String>> list = null;
		if("role".equals(key)){
			list = this.sysRoleDao.getSysRoleJpa().getRoleOptions();
		}
		return list;
	}
}
