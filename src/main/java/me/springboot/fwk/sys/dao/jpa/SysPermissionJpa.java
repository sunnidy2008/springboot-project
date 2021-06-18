package me.springboot.fwk.sys.dao.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import me.springboot.fwk.sys.vo.SysPermissionVO;

@Repository
public interface SysPermissionJpa extends JpaRepository<SysPermissionVO, java.lang.String> {

	public SysPermissionVO findBycUrlAndCMethod(String url,String method);
	
	public void deleteBycUrlAndCMethod(String url,String method);
	
	@Modifying
	@Query(value=	" select a.* from t_sys_permission a"+
					" right join "+
					"   t_sys_role_permission b"+
					" on "+
					"   a.c_pk_id = b.c_role_id "+
					" right join "+
					"   t_sys_user_role c"+
					" on "+
					"   b.c_role_id=c.c_role_id "+
					" where "+
					"   c.c_user_id=:userId",nativeQuery=true)
	public List<SysPermissionVO> selectByUserId(@Param("userId") Long userId); 
	
	
	@Modifying
	@Query(value=	" select "+
					"   d.c_enname "+
					" from "+
					"   t_sys_permission b,t_sys_role_permission c,t_sys_role d "+
					" where "+
					"   b.c_url=:url and (b.c_method=:method or b.c_method is null or b.c_method='')"+
					"   and b.c_pk_id = c.c_permission_id"+
					"	and c.c_role_id=d.c_pk_id",nativeQuery=true)
	public List<String> select(@Param("url")String url,@Param("method")String method);
}
