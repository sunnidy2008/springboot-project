package me.springboot.fwk.websocket.dao.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.springboot.fwk.websocket.vo.SysWsMessageVO;

@Repository
public interface SysWsMessageJpa extends JpaRepository<SysWsMessageVO, java.lang.String> {

}
