package me.springboot.fwk.sys.dao.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import me.springboot.fwk.sys.vo.SysUserRoleVO;

@Repository
public interface SysUserRoleJpa extends JpaRepository<SysUserRoleVO, java.lang.String> {

	public List<SysUserRoleVO> findBycUserId(String userId);
	
	@Query(value="select c_role_id from t_sys_user_role where c_user_id=:userId",nativeQuery=true)
	public List<String> getUserRoleList(@Param("userId")String userId);
}
