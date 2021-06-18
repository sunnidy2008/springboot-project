package me.springboot.fwk.sys.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import lombok.Getter;
import lombok.Setter;
import me.springboot.fwk.db.DbUtils;
import me.springboot.fwk.db.page.PageResult;
import me.springboot.fwk.result.R;
import me.springboot.fwk.result.Result;
import me.springboot.fwk.sys.UserUtils;
import me.springboot.fwk.sys.service.SysRolePermissionService;
import me.springboot.fwk.sys.vo.SysRolePermissionVO;
import me.springboot.fwk.utils.BeanUtils;
import me.springboot.fwk.utils.VoUtils;

@RestController
@Getter
@Setter
public class SysRolePermissionController{

	@Autowired
	private SysRolePermissionService sysRolePermissionService;
	@Autowired
	private DbUtils dbUtils;
	
//	//按主键查找数据对象
//	@PostMapping(name="按主键查找角色权限数据",value="/api/sysRolePermission/detail")
//	public Result detail(@RequestBody JSONObject params){
//		String id = params.getString("id");
//		SysRolePermissionVO bean = this.getSysRolePermissionService().findByPrimaryKey(id);
//		return R.success(JSONObject.toJSON(bean));
//	}
	
//	//按主键删除对象
//	@PostMapping(name="按主键删除角色权限数据",value="/api/sysRolePermission/delete")
//	@Transactional
//	public Result delete(@RequestBody JSONObject params){
//		String id = params.getString("id");
//		this.getSysRolePermissionService().deleteByPrimaryKey(id);
//		return R.success();
//	}
	
	//保存或更新对象
	@PostMapping(name="保存或更新角色权限数据",value="/api/sysRolePermission")
	@Transactional
	public Result saveOrUpdate(@RequestBody JSONObject params) throws Exception{
		String roleId = params.getString("roleId");
		JSONArray selectedPermission = params.getJSONArray("selectedPermission");
		
		String deleteSql ="delete from t_sys_role_permission where C_ROLE_ID=?";
		this.dbUtils.updateSql(deleteSql, roleId);
		
		Date now = new Date();
		String userId = UserUtils.getUser().getId();
		List<SysRolePermissionVO> list = new ArrayList<SysRolePermissionVO>();
		for(int i=0;i<selectedPermission.size();i++){
			SysRolePermissionVO bean = new SysRolePermissionVO();
			bean.setcPkId(UUID.randomUUID().toString());
			bean.setcPermissionId(selectedPermission.getString(i));
			bean.setcRoleId(roleId);
			VoUtils.touch(bean, now, userId);
			list.add(bean);
		}
		
		this.getSysRolePermissionService().getSysRolePermissionDao().getSysRolePermissionJpa().saveAll(list);
		return R.success();
	}
	
//	//列表查询
//	@PostMapping(name="角色权限数据列表查询",value="/api/sysRolePermission/list")
//	public Result get(@RequestBody JSONObject params) throws Exception{
//		String sql = " select "+
//					 "	 C_PK_ID as cPkId, "+
//					 "	 C_ROLE_ID as cRoleId, "+
//					 "	 C_PERMISSION_ID as cPermissionId, "+
//					 "	 date_format(T_CRT_TM,'%Y-%m-%d %H:%i:%S') as tCrtTm, "+
//					 "	 date_format(T_UPD_TM,'%Y-%m-%d %H:%i:%S') as tUpdTm, "+
//					 "	 C_CRT_ID as cCrtId, "+
//					 "	 C_UPD_ID as cUpdId "+
//					 " from "+
//					 " 	 t_sys_role_permission"+
//					 " where "+
//					 "	 C_PK_ID = '#cPkId' "+
//					 "	 and C_ROLE_ID = '#cRoleId' "+
//					 "	 and C_PERMISSION_ID = '#cPermissionId' "+
//					 "	 and T_CRT_TM = '#tCrtTm' "+
//					 "	 and T_UPD_TM = '#tUpdTm' "+
//					 "	 and C_CRT_ID = '#cCrtId' "+
//					 "	 and C_UPD_ID = '#cUpdId' ";
//		PageResult pageResult = dbUtils.queryPageSql(sql,params);
//		return R.success(pageResult);
//	}
	
	//列表查询
	@PostMapping(name="按角色id查询权限id集合供角色页面使用",value="/api/sysRolePermission/listByRoleId")
	public Result listByRoleId(@RequestBody JSONObject params) throws Exception{
		String roleId = params.getString("roleId");
		String sql =" select "+
					"	 C_PERMISSION_ID as cPermissionId "+
					" from "+
					" 	 t_sys_role_permission"+
					" where "+
					"	 C_ROLE_ID = ? ";
		List<Map<String,Object>> list = dbUtils.querySql(sql,roleId);
		List<String> resultList = new ArrayList<String>();
		for(Map<String,Object> map:list){
			resultList.add(map.get("cPermissionId").toString());
		}
		return R.success(resultList);
	}
}
