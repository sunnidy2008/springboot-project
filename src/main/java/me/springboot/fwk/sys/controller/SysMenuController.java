package me.springboot.fwk.sys.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
import me.springboot.fwk.db.page.PageResult;
import me.springboot.fwk.result.R;
import me.springboot.fwk.result.Result;
import me.springboot.fwk.sys.service.SysMenuService;
import me.springboot.fwk.sys.vo.SysMenuVO;
import me.springboot.fwk.utils.BeanUtils;

@RestController
@Getter
@Setter
public class SysMenuController{

	@Autowired
	private SysMenuService sysMenuService;
	@Autowired
	private DbUtils dbUtils;
	
	//按主键查找数据对象
	@PostMapping(name="按主键查询菜单详细",value="/api/sysMenu/detail")
	public Result detail(@RequestBody JSONObject params){
		String id = params.getString("id");
		SysMenuVO bean = this.getSysMenuService().findByPrimaryKey(id);
		return R.success(JSONObject.toJSON(bean));
	}
	
	//按主键删除对象
	@PostMapping(name="按主键删除菜单数据",value="/api/sysMenu/delete")
	@Transactional
	public Result delete(@RequestBody JSONObject params){
//		String id = params.getString("id");
//		this.getSysMenuService().deleteByPrimaryKey(id);
		
		String cMenuCode = params.getString("cMenuCode");
		String sql = "DELETE FROM T_SYS_MENU WHERE C_PARENT_CODE=? OR C_MENU_CODE=?";
		this.dbUtils.updateSql(sql, cMenuCode,cMenuCode);
		return R.success();
	}
	
	//保存或更新对象
	@PostMapping(name="保存或更新菜单表数据",value="/api/sysMenu")
	@Transactional
	public Result saveOrUpdate(@RequestBody JSONObject params) throws Exception{
		SysMenuVO bean = new SysMenuVO();
		BeanUtils.map2Bean(params,bean);
		this.getSysMenuService().saveOrUpdate(bean);
		return R.success(JSONObject.toJSON(bean));
	}
	
//	//列表查询
//	@PostMapping(name="",value="/api/sysMenu/list")
//	public Result list(@RequestBody JSONObject params) throws Exception{
//		String sql = " select "+
//					 "	 C_MENU_CODE as cMenuCode, "+
//					 "	 C_MENU_NAME as cMenuName, "+
//					 "	 C_URL as cUrl, "+
//					 "	 N_NUM as nNum, "+
//					 "	 N_LEVEL as nLevel, "+
//					 "	 C_PARENT_CODE as cParentCode, "+
//					 "	 C_STATUS as cStatus, "+
//					 "	 date_format(D_INVALID_TIME,'%Y-%m-%d %H:%i:%S') as dInvalidTime "+
//					 " from "+
//					 " 	 t_sys_menu"+
//					 " where "+
//					 "	 C_MENU_CODE = '#cMenuCode' "+
//					 "	 and C_MENU_NAME = '#cMenuName' "+
//					 "	 and C_URL = '#cUrl' "+
//					 "	 and N_NUM = '#nNum' "+
//					 "	 and N_LEVEL = '#nLevel' "+
//					 "	 and C_PARENT_CODE = '#cParentCode' "+
//					 "	 and C_STATUS = '#cStatus' "+
//					 "	 and D_INVALID_TIME = '#dInvalidTime' ";
//		PageResult pageResult = dbUtils.querySql(sql,params);
//		return R.success(pageResult);
//	}
	
	 /**
     * 新建菜单编码
     * @param json
     * @return
     * @throws Exception
     */
    @PostMapping(name="获取新的菜单编码",value="/api/sysMenu/generateMenuCode")
    public Result generateMenuCode(@RequestBody JSONObject json) throws Exception{
    	String generatedCode = null;
    	String parentCode = json.getString("cParentCode");
    	String sql = "SELECT MAX(C_Menu_Code) as MAXCODE FROM T_SYS_MENU WHERE C_Parent_Code=?";
    	List<Map<String,Object>> result = dbUtils.querySql(sql, parentCode);
    	if(result == null || result.size() == 0){
    		generatedCode = parentCode + "01";
    	}else{
    		String maxCode = String.valueOf(result.get(0).get("MAXCODE"));
    		if(maxCode == null || "null".equals(maxCode.toLowerCase())){
    			if("0".equals(parentCode)){
	    			generatedCode = "01";
    			}else{
    				generatedCode = parentCode + "01";
    			}
    		}else{
	    		int length = maxCode.length();
	    		String lastTwo = maxCode.substring(length - 2,length);
	    		Integer intValue = Integer.valueOf(lastTwo);
	    		intValue = intValue + 1;
	    		generatedCode = intValue < 10 ? "0" + intValue : String.valueOf(intValue);
	    		if(!"0".equals(parentCode)){
	    			generatedCode = parentCode + generatedCode;
	    		}
    		}
    	}
    	JSONObject resultJSON = new JSONObject();
    	resultJSON.put("generatedCode", generatedCode);
    	return R.success(resultJSON);
    }
    
    /**
     * 列表查询
     * @param params
     * @return
     * @throws Exception
     */
    @PostMapping(name="获取菜单树形结构数据",value="/api/sysMenu/list")
    public Result list(@RequestBody JSONObject params) throws Exception{
    	JSONArray rootList = getMenuListByLevelAndPCode(0,null);
    	mountChildren(rootList);
		return R.success(rootList);
    }
    
    /**
     * 挂载子级菜单
     * @param parentList
     * @throws Exception
     */
    private void mountChildren(JSONArray parentList) throws Exception{
    	int pSize = parentList.size();
    	for(int i = 0; i < pSize; i++){
    		JSONObject pObject = parentList.getJSONObject(i);
//    		pObject.put("label", pObject.get("cMenuName"));
    		Set<String> keys = pObject.keySet();
    		for(String key : keys){
    			if(pObject.get(key) == null/* || pObject.get(key) instanceof JSONNull*/){
    				pObject.put(key, "");
    			}
    		}
    		String pCode = pObject.getString("cMenuCode");
    		int pLevel = pObject.getIntValue("nLevel");
    		JSONArray childrenList = getMenuListByLevelAndPCode(pLevel + 1, pCode);
    		if(childrenList != null){
    			pObject.put("childrenList", childrenList);
    			mountChildren(pObject.getJSONArray("childrenList"));
    		}
    	}
    }
    
    /**
     * 根据层级和父级编码查询菜单
     * @param level
     * @param parentCode
     * @return
     * @throws Exception
     */
    private JSONArray getMenuListByLevelAndPCode(int level,String parentCode) throws Exception{
    	List<Map<String,Object>> resultList;
		String sql = " select "+
					 "	 C_MENU_CODE as cMenuCode, "+
					 "	 C_MENU_NAME as cMenuName, "+
					 "	 C_URL as cUrl, "+
					 "	 N_NUM as nNum, "+
					 "	 N_LEVEL as nLevel, "+
					 "	 C_PARENT_CODE as cParentCode, "+
					 "	 C_STATUS as cStatus, "+
					 "	 date_format(D_INVALID_TIME,'%Y-%m-%d %H:%i:%S') as dInvalidTime "+
					 " from "+
					 " 	 t_sys_menu"+
					 " where "+
					 "	 N_LEVEL = ? ";
    	if(StringUtils.isNotEmpty(parentCode)){
    		sql = sql + " AND C_PARENT_CODE=? ORDER BY N_NUM";
    		resultList = dbUtils.querySql(sql,level,parentCode);
    	}else{
    		sql = sql + " ORDER BY N_NUM";
    		resultList = dbUtils.querySql(sql,level);
    	}
    	if(resultList != null && resultList.size() > 0){
    		JSONArray array = new JSONArray();
    		for(Map map : resultList){
    			JSONObject menu = (JSONObject) JSONObject.toJSON(map);
    			array.add(menu);
    		}
    		return array;
    	}
    	return null;
    }
}
