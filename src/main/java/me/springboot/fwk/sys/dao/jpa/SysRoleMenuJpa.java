package me.springboot.fwk.sys.dao.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.springboot.fwk.sys.vo.SysRoleMenuVO;

@Repository
public interface SysRoleMenuJpa extends JpaRepository<SysRoleMenuVO, java.lang.String> {

}
