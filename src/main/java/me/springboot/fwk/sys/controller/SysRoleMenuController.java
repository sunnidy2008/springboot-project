package me.springboot.fwk.sys.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import lombok.Getter;
import lombok.Setter;
import me.springboot.fwk.db.DbUtils;
import me.springboot.fwk.result.R;
import me.springboot.fwk.result.Result;
import me.springboot.fwk.sys.UserUtils;
import me.springboot.fwk.sys.dao.SysRoleMenuDao;
import me.springboot.fwk.sys.service.SysRoleMenuService;
import me.springboot.fwk.sys.vo.SysRoleMenuVO;
import me.springboot.fwk.utils.VoUtils;

@RestController
@Getter
@Setter
public class SysRoleMenuController{

	@Autowired
	private SysRoleMenuService sysRoleMenuService;
	@Autowired
	private SysRoleMenuDao sysRoleMenuDao;
	@Autowired
	private DbUtils dbUtils;
	
//	//按主键查找数据对象
//	@PostMapping(value="/api/sysRoleMenu/detail",name="按主键获取角色菜单的详细记录")
//	public Result detail(@RequestBody JSONObject params){
//		String id = params.getString("id");
//		SysRoleMenuVO bean = this.getSysRoleMenuService().findByPrimaryKey(id);
//		return R.success(JSONObject.toJSON(bean));
//	}
	
//	//按主键删除对象
//	@PostMapping(value="/api/sysRoleMenu/delete",name="按主键删除角色菜单的指定记录")
//	@Transactional
//	public Result delete(@RequestBody JSONObject params){
//		String id = params.getString("id");
//		this.getSysRoleMenuService().deleteByPrimaryKey(id);
//		return R.success();
//	}
	
	//保存或更新对象
	@PostMapping(value="/api/sysRoleMenu",name="保存或更新数据到角色菜单")
	@Transactional
	public Result saveOrUpdate(@RequestBody JSONObject params) throws Exception{
		JSONArray arr = params.getJSONArray("selectedMenu");
    	String roleId = params.getString("roleId");
    	if(StringUtils.isEmpty(roleId)){
    		return R.error(0,"传入的角色ID为空");
    	}
    	String sql = "DELETE FROM T_SYS_ROLE_MENU WHERE C_ROLE_ID=?";
    	this.dbUtils.updateSql(sql, roleId);
    	int length = arr.size();
    	Date now = new Date();
    	String userId = UserUtils.getUser().getId();
    	for(int i = 0; i < length; i++){
    		SysRoleMenuVO bean = new SysRoleMenuVO();
    		String menuCode = arr.getString(i);
    		bean.setcPkId(UUID.randomUUID().toString());
    		bean.setcRoleId(roleId);
    		bean.setcMenuCode(menuCode);
    		bean.setcStatus("1");
    		VoUtils.touch(bean, now, userId);
    		this.sysRoleMenuDao.getSysRoleMenuJpa().save(bean);
    	}
        return R.success();
	}
	
//	//列表查询
//	@PostMapping(value="/api/sysRoleMenu/list",name="列表查询角色菜单的数据")
//	public Result list(@RequestBody JSONObject params) throws Exception{
//		String sql = " select "+
//					 "	 C_PK_ID as cPkId, "+
//					 "	 C_ROLE_ID as cRoleId, "+
//					 "	 C_MENU_CODE as cMenuCode, "+
//					 "	 C_STATUS as cStatus, "+
//					 "	 date_format(T_CRT_TM,'%Y-%m-%d %H:%i:%S') as tCrtTm, "+
//					 "	 date_format(T_UPD_TM,'%Y-%m-%d %H:%i:%S') as tUpdTm, "+
//					 "	 C_CRT_ID as cCrtId, "+
//					 "	 C_UPD_ID as cUpdId "+
//					 " from "+
//					 " 	 t_sys_role_menu"+
//					 " where "+
//					 "	 C_PK_ID = '#cPkId' "+
//					 "	 and C_ROLE_ID = '#cRoleId' "+
//					 "	 and C_MENU_CODE = '#cMenuCode' "+
//					 "	 and C_STATUS = '#cStatus' "+
//					 "	 and T_CRT_TM = '#tCrtTm' "+
//					 "	 and T_UPD_TM = '#tUpdTm' "+
//					 "	 and C_CRT_ID = '#cCrtId' "+
//					 "	 and C_UPD_ID = '#cUpdId' ";
//		PageResult pageResult = dbUtils.querySql(sql,params);
//		return R.success(pageResult);
//	}
	
//	/**
//     * 列表查询
//     * @param params
//     * @return
//     * @throws Exception
//     */
//    @RequestMapping(value="/api/sysRoleMenu/list",name="列表查询角色菜单的数据")
//    public Result list(@RequestBody JSONObject params) throws Exception{
//    	String cRoleId = params.getString("cRoleId");
//    	JSONArray rootList = getMenuListByLevelAndPCode(0,null,cRoleId);
//    	mountChildren(rootList,cRoleId,true);
//		return R.success(rootList);
//    }
//    
//    @RequestMapping(value="/api/sysRoleMenu/allMenuTree",name="列表查询所有菜单数据，以树形结构呈现")
//    public Result allMenuTree(@RequestBody JSONObject params) throws Exception{
//    	JSONArray rootList = getMenuListByLevelAndPCode(0,null,null);
//    	mountChildren(rootList,null,false);
//		return R.success(rootList);
//    }
    
    @RequestMapping(value="/api/sysRoleMenu/list",name="查询角色拥有的菜单数据")
    public Result list(@RequestBody JSONObject params) throws Exception{
    	String roleId = params.getString("cRoleId");
//    	JSONArray rootList = getMenuListByLevelAndPCode(0,null,roleId);
//    	mountChildren(rootList,roleId,true);
    	String sql="select C_MENU_CODE as menuCode from t_sys_role_menu where C_ROLE_ID=?";
    	List<Map<String,Object>> list = this.dbUtils.querySql(sql, roleId);
    	List<String> resultList = new ArrayList<String>();
    	for(Map<String,Object> map : list){
    		resultList.add(map.get("menuCode").toString());
    	}
		return R.success(resultList);
    }
    
    @RequestMapping(value="/api/sysRoleMenu/menu",name="初始化首页左侧的菜单")
    public Result menu(@RequestBody JSONObject params) throws Exception{
    	List<String> roleList = UserUtils.getUser().getRole();
    	String sql="select a.C_MENU_CODE as menuCode from t_sys_role_menu a,t_sys_role b where b.C_ENNAME in (:roles) and a.C_ROLE_ID=b.C_PK_ID";
    	Map paramMap = new HashMap();
    	paramMap.put("roles", roleList);
    	List<Map<String,Object>> list = this.dbUtils.querySql(sql, paramMap);
//    	List<String> resultList = new ArrayList<String>();
    	Set<String> set = new HashSet<String>();
    	String tmp =null;
    	int maxCodeLength=0;
    	for(Map<String,Object> map : list){
    		tmp = map.get("menuCode").toString();
    		set.add(tmp);
    		if(tmp.length()>2){
    			set.add(tmp.substring(0,tmp.length()-2));
    		}
    		if(maxCodeLength<tmp.length()){
    			maxCodeLength = tmp.length();
    		}
    	}
    	
    	String sql2="select C_MENU_CODE as code,C_URL as url,C_MENU_NAME as title from t_sys_menu where C_MENU_CODE in (:menuCode) order by N_NUM asc";
    	Map paramMap2 = new HashMap<String,Object>();
    	paramMap2.put("menuCode", new ArrayList<>(set));
    	List<Map<String,Object>> list2 = this.dbUtils.querySql(sql2, paramMap2);
    	JSONArray menus = new JSONArray();
    	for(int i=2;i<=maxCodeLength;i=i+2){
    		createMenu(menus,list2,i);
    	}
		return R.success(menus);
    }
    
    private void createMenu(JSONArray menus,List<Map<String,Object>> list,int level){
    	String code;
    	JSONObject menu = new JSONObject();
    	boolean equals=false;
    	for(Map<String,Object> map : list){
    		code = map.get("code").toString();
    		if(code.length()!=level){
    			continue;
    		}
    		equals=false;
    		for(int i=0;i<menus.size();i++){
    			menu = menus.getJSONObject(i);
    			if(menu.getString("code").equals(code.substring(0,code.length()-2))){
    				menu.getJSONArray("children").add(map);
    				equals=true;
    				break;
    			}
    		}
    		if(!equals){
    			menu = new JSONObject();
    			menu.putAll(map);
    			menu.put("children", new JSONArray());
    			menus.add(menu);
    		}
    	}
    }
    
//    /**
//     * 挂载子级菜单
//     * @param parentList
//     * @param roleId
//     * @param isGetMenu  指定是否加载菜单，还是权限设置, true代表加载权限菜单，false代表加载权限设置
//     * @throws Exception
//     */
//    private void mountChildren(JSONArray parentList,String roleId,boolean isGetMenu) throws Exception{
//    	if(parentList == null){
//    		return;
//    	}
//    	int pSize = parentList.size();
//    	for(int i = 0; i < pSize; i++){
//    		JSONObject pObject = parentList.getJSONObject(i);
////    		pObject.put("label", pObject.get("cMenuName"));
////    		Set<String> keys = pObject.keySet();
////    		for(String key : keys){
////    			if(pObject.get(key) == null){
////    				pObject.put(key, "");
////    			}
////    		}
//    		String pCode = pObject.getString("cMenuCode");
//    		int pLevel = pObject.getIntValue("nLevel");
//    		JSONArray childrenList = null;
//    		if(isGetMenu){
//    			childrenList = getRoleMenuByGroup(pLevel + 1, pCode,roleId);
//    		}else{
//    			childrenList = getMenuListByLevelAndPCode(pLevel + 1, pCode,roleId);
//    		}
//    		if(childrenList != null){
//    			pObject.put(isGetMenu ? "list" : "childrenList", childrenList);
//    			mountChildren(pObject.getJSONArray(isGetMenu ? "list" : "childrenList"),roleId,isGetMenu);
//    		}
//    	}
//    }
//    
//    /**
//     * 根据层级和父级编码查询菜单
//     * @param level
//     * @param parentCode
//     * @return
//     * @throws Exception
//     */
//    private JSONArray getMenuListByLevelAndPCode(int level,String parentCode,String roleId) throws Exception{
//    	List<Map<String, Object>> resultList;
//    	String sql =" SELECT "+
//			    	" 	C_MENU_CODE as cMenuCode,"+
//			    	" 	C_MENU_NAME as cMenuName,"+
//			    	" 	C_URL as cUrl,"+
//			    	" 	N_NUM as nNum,"+
//			    	" 	N_LEVEL as nLevel,"+
//			    	" 	C_PARENT_CODE as cParentCode,"+
//			    	" 	C_STATUS as cStatus,"+
//			    	" 	D_INVALID_TIME as dInValidTime,"+
//			    	" 	CASE "+
//			    	" 		(SELECT COUNT(1) FROM T_SYS_ROLE_MENU r where r.C_ROLE_ID = ? AND r.C_MENU_CODE = m.C_MENU_CODE) "+
//			    	" 	WHEN 0 then 'false' "+
//			    	" 	ELSE 'true' "+
//			    	" 	END as isValid"+
//			    	" FROM "+
//			    	" 	T_SYS_MENU m "+
//			    	" WHERE "+
//			    	" 	N_LEVEL=?";
//    	if(StringUtils.isNotEmpty(parentCode)){
//    		sql = sql+ " AND C_PARENT_CODE=? ORDER BY NNum";
//    		resultList = this.dbUtils.querySql(sql,roleId,level,parentCode);
//    	}else{
//    		sql = sql+" ORDER BY N_NUM";
//    		resultList = this.dbUtils.querySql(sql,roleId,level);
//    	}
//    	if(resultList != null && resultList.size() > 0){
//    		JSONArray array = new JSONArray();
//    		for(Map map : resultList){
//    			JSONObject menu = (JSONObject) JSONObject.toJSON(map);
//    			array.add(menu);
//    		}
//    		return array;
//    	}
//    	return null;
//    }
//    
//    private JSONArray getRoleMenuByGroup(int level,String parentCode,String roleId) throws Exception{
//    	List<Map<String, Object>> resultList;
//    	String sql =" SELECT distinct "+
//	    			" 	m.C_MENU_CODE as cMenuCode,"+
//	    			" 	m.C_MENU_NAME as title,"+
//	    			" 	m.C_URL as cUrl,"+
//	    			" 	m.N_LEVEL as nLevel,"+
//	    			" 	r.C_ROLE_ID as cRoleId,"+
//	    			" 	'' as icon,"+
//	    			" 	m.N_NUM as nNum"+
//	    			" FROM "+
//	    			" 	T_SYS_ROLE_MENU r "+
//	    			" LEFT JOIN "+
//	    			" 	T_SYS_MENU m "+
//	    			" ON "+
//	    			" 	r.C_MENU_CODE = m.C_MENU_CODE "+
//	    			" WHERE "+
//	    			" 	m.N_LEVEL=? ";
//    	String userId = UserUtils.getUser().getId();
//    	if(StringUtils.isNotEmpty(parentCode)){
//    		sql=sql+" AND m.C_PARENT_CODE=? ORDER BY m.N_NUM";
//    		resultList = this.dbUtils.querySql(sql,level,parentCode);
//    	}else{
//    		sql=sql+" ORDER BY m.N_NUM";
//    		resultList = this.dbUtils.querySql(sql.toString(),level);
//    	}
//    	if(resultList != null && resultList.size() > 0){
//    		JSONArray array = new JSONArray();
//    		String addedCode = "";
//    		for(Map map : resultList){
//    			if(!"SuperAdmin".equals(userId) && !roleId.equals(map.get("cRoleId"))){
//	    			continue;
//    	    	}
//    			JSONObject menu = (JSONObject) JSONObject.toJSON(map);
//    			if(!addedCode.contains(menu.getString("cMenuCode"))){
//    				array.add(menu);
//    				addedCode += (menu.getString("cMenuCode") + "#");
//    			}
//    		}
//    		return array;
//    	}
//    	return null;
//    }
}
