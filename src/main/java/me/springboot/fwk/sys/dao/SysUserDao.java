package me.springboot.fwk.sys.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.Getter;
import lombok.Setter;
import me.springboot.fwk.sys.dao.jpa.SysUserJpa;

@Repository
@Getter
@Setter
public class SysUserDao {

	@Autowired
	private SysUserJpa sysUserJpa;
	
	
}
