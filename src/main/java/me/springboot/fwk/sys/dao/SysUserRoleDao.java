package me.springboot.fwk.sys.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.Getter;
import lombok.Setter;
import me.springboot.fwk.db.DbUtils;
import me.springboot.fwk.sys.dao.jpa.SysUserRoleJpa;
@Repository
@Getter
@Setter
public class SysUserRoleDao{

	@Autowired
	private SysUserRoleJpa sysUserRoleJpa;
	@Autowired
	private DbUtils dbUtils;
}
