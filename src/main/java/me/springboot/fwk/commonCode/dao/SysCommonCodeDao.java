package me.springboot.fwk.commonCode.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import me.springboot.fwk.commonCode.dao.jpa.SysCommonCodeJpa;
import lombok.Getter;
import lombok.Setter;
import me.springboot.fwk.db.DbUtils;
@Repository
@Getter
@Setter
public class SysCommonCodeDao{

	@Autowired
	private SysCommonCodeJpa sysCommonCodeJpa;
	@Autowired
	private DbUtils dbUtils;
}
