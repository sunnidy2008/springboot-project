package me.springboot.fwk.log.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import me.springboot.fwk.log.dao.jpa.SysLogJpa;
import lombok.Getter;
import lombok.Setter;
import me.springboot.fwk.db.DbUtils;
@Repository
@Getter
@Setter
public class SysLogDao{

	@Autowired
	private SysLogJpa sysLogJpa;
	@Autowired
	private DbUtils dbUtils;
}
