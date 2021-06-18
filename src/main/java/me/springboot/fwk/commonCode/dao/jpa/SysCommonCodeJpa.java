package me.springboot.fwk.commonCode.dao.jpa;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import me.springboot.fwk.commonCode.vo.SysCommonCodeVO;

public interface SysCommonCodeJpa extends JpaRepository<SysCommonCodeVO, java.lang.String> {

	@Query(value="select c_code as code,c_text as text,c_enabled as enabled,c_css as css from t_sys_common_code where c_parent_code=:code order by n_seq asc",nativeQuery=true)
	public List<Map<String,String>> get(@Param("code") String code);
}
