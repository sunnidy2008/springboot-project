package me.springboot.fwk.sys.dao.jpa;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import me.springboot.fwk.sys.vo.SysRoleVO;

@Repository
public interface SysRoleJpa extends JpaRepository<SysRoleVO, java.lang.String> {

	@Query(value="select c_pk_id as code ,c_name as text from t_sys_role",nativeQuery=true)
	public List<Map<String,String>> getRoleOptions();
	
	@Modifying
	@Query(value=	" select a.* from t_sys_role a"+
					" right join "+
					"   t_sys_user_role c"+
					" on "+
					"   a.c_pk_id=c.c_role_id "+
					" where "+
					"   c.c_user_id=:userId",nativeQuery=true)
	public List<SysRoleVO> selectByUserId(@Param("userId")String userId); 
}
