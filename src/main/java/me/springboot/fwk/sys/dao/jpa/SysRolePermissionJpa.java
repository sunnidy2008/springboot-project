package me.springboot.fwk.sys.dao.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import me.springboot.fwk.sys.vo.SysRolePermissionVO;

@Repository
public interface SysRolePermissionJpa extends JpaRepository<SysRolePermissionVO, java.lang.String> {

	public void deleteBycPermissionId(String permissionId);
	
	@Query(value="select * from t_sys_role_permission where c_role_id=:roleId",nativeQuery=true)
	public List<SysRolePermissionVO> findByCRoleId(String roleId);
}
