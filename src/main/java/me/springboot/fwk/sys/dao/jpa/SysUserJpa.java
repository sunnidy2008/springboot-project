package me.springboot.fwk.sys.dao.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import me.springboot.fwk.sys.vo.SysUserVO;

@Repository
public interface SysUserJpa extends JpaRepository<SysUserVO, String> {

//	@Query(value="select * from t_sys_user where c_username=:username and c_enabled='1'",nativeQuery=true)
	public List<SysUserVO> findBycUsername(String username);
}
