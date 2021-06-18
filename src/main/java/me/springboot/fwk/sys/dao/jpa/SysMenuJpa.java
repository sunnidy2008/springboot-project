package me.springboot.fwk.sys.dao.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.springboot.fwk.sys.vo.SysMenuVO;

@Repository
public interface SysMenuJpa extends JpaRepository<SysMenuVO, java.lang.String> {

}
