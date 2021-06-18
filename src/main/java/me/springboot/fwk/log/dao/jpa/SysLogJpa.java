package me.springboot.fwk.log.dao.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.springboot.fwk.log.vo.SysLogVO;

@Repository
public interface SysLogJpa extends JpaRepository<SysLogVO, java.lang.String> {

}
