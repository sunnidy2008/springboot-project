package me.springboot.fwk.sys.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import me.springboot.fwk.sys.dao.jpa.SysAddressJpa;
import lombok.Getter;
import lombok.Setter;
import me.springboot.fwk.db.DbUtils;
@Repository
@Getter
@Setter
public class SysAddressDao{

	@Autowired
	private SysAddressJpa sysAddressJpa;
	@Autowired
	private DbUtils dbUtils;
}
