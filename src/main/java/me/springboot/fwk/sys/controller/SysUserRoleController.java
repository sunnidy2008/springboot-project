package me.springboot.fwk.sys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import lombok.Getter;
import lombok.Setter;
import me.springboot.fwk.db.DbUtils;
import me.springboot.fwk.db.page.PageResult;
import me.springboot.fwk.result.R;
import me.springboot.fwk.result.Result;
import me.springboot.fwk.sys.service.SysUserRoleService;
import me.springboot.fwk.sys.vo.SysUserRoleVO;
import me.springboot.fwk.utils.BeanUtils;

@RestController
@Getter
@Setter
public class SysUserRoleController{

	@Autowired
	private SysUserRoleService sysUserRoleService;
	@Autowired
	private DbUtils dbUtils;
	
//	//按主键查找数据对象
//	@PostMapping(name="按主键查找用户角色",value="/api/sysUserRole/detail")
//	public Result detail(@RequestBody JSONObject params){
//		String id = params.getString("id");
//		SysUserRoleVO bean = this.getSysUserRoleService().findByPrimaryKey(id);
//		return R.success(JSONObject.toJSON(bean));
//	}
	
//	//按主键删除对象
//	@PostMapping(name="按主键删除制定用户角色",value="/api/sysUserRole/delete")
//	@Transactional
//	public Result delete(@RequestBody JSONObject params){
//		String id = params.getString("id");
//		this.getSysUserRoleService().deleteByPrimaryKey(id);
//		return R.success();
//	}
	
//	//保存或更新对象
//	@PostMapping(name="保存或更新用户角色",value="/api/sysUserRole")
//	@Transactional
//	public Result saveOrUpdate(@RequestBody JSONObject params) throws Exception{
//		SysUserRoleVO bean = new SysUserRoleVO();
//		BeanUtils.map2Bean(params,bean);
//		this.getSysUserRoleService().saveOrUpdate(bean);
//		return R.success(JSONObject.toJSON(bean));
//	}
	
	//列表查询
	@PostMapping(name="用户角色列表查询",value="/api/sysUserRole/list")
	public Result get(@RequestBody JSONObject params) throws Exception{
		String sql = " select "+
					 "	 C_PK_ID as cPkId, "+
					 "	 C_USER_ID as cUserId, "+
					 "	 C_ROLE_ID as cRoleId, "+
					 "	 date_format(T_CRT_TM,'%Y-%m-%d %H:%i:%S') as tCrtTm, "+
					 "	 date_format(T_UPD_TM,'%Y-%m-%d %H:%i:%S') as tUpdTm, "+
					 "	 C_CRT_ID as cCrtId, "+
					 "	 C_UPD_ID as cUpdId "+
					 " from "+
					 " 	 t_sys_user_role"+
					 " where "+
					 "	 C_PK_ID = '#cPkId' "+
					 "	 and C_USER_ID = '#cUserId' "+
					 "	 and C_ROLE_ID = '#cRoleId' "+
					 "	 and T_CRT_TM = '#tCrtTm' "+
					 "	 and T_UPD_TM = '#tUpdTm' "+
					 "	 and C_CRT_ID = '#cCrtId' "+
					 "	 and C_UPD_ID = '#cUpdId' ";
		PageResult pageResult = dbUtils.queryPageSql(sql,params);
		return R.success(pageResult);
	}
}
