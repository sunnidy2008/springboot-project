package me.springboot.fwk.sys.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.Getter;
import lombok.Setter;
import me.springboot.fwk.db.DbUtils;
import me.springboot.fwk.sys.dao.jpa.SysMenuJpa;
@Repository
@Getter
@Setter
public class SysMenuDao{

	@Autowired
	private SysMenuJpa sysMenuJpa;
	@Autowired
	private DbUtils dbUtils;
}
