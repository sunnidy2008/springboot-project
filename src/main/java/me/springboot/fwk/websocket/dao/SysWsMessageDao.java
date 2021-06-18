package me.springboot.fwk.websocket.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import me.springboot.fwk.websocket.dao.jpa.SysWsMessageJpa;
import lombok.Getter;
import lombok.Setter;
import me.springboot.fwk.db.DbUtils;
@Repository
@Getter
@Setter
public class SysWsMessageDao{

	@Autowired
	private SysWsMessageJpa sysWsMessageJpa;
	@Autowired
	private DbUtils dbUtils;
}
