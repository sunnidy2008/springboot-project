package me.springboot.fwk.sys.controller;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.springboot.fwk.db.DbUtils;
import me.springboot.fwk.db.page.PageResult;
import me.springboot.fwk.result.R;
import me.springboot.fwk.result.Result;
import me.springboot.fwk.sys.dao.SysRolePermissionDao;
import me.springboot.fwk.sys.service.SysPermissionService;
import me.springboot.fwk.sys.vo.SysPermissionVO;
import me.springboot.fwk.utils.BeanUtils;
import me.springboot.fwk.utils.SpringBootUtils;

@RestController
@Getter
@Setter
public class SysPermissionController{

	@Autowired
	private SysPermissionService sysPermissionService;
	@Autowired
	private SysRolePermissionDao sysRolePermissionDao;
	@Autowired
	private DbUtils dbUtils;
	
	//按主键查找数据对象
	@Transactional
	@PostMapping(name="同步所有restful的url请求数据到t_sys_permission表中",value="/api/sysPermission/collect")
	public Result collect() throws ClassNotFoundException{
		ApplicationContext applicationContext = SpringBootUtils.getApplicationContext();
		//获取自定义注解的配置
	    final Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(RestController.class);
	    List<PermissionBo> list = new ArrayList<PermissionBo>();
	    
	    List<SysPermissionVO> dbList = this.sysPermissionService.getSysPermissionDao().getSysPermissionJpa().findAll();
	    
	    for (String key : beansWithAnnotation.keySet()) {
	    	Method[] methods = Class.forName(beansWithAnnotation.get(key).getClass().getName()).getMethods();
	    	for (Method method : methods) {
	    		//获取指定方法上的注解的属性
	    		final RequestMapping requestAnnotation = AnnotationUtils.findAnnotation(method, RequestMapping.class);
	    		if (null != requestAnnotation) {
		        	String values []=requestAnnotation.value();
		        	String name = requestAnnotation.name();
		        	for(String item : values){
		        		savePermission(name, item,"POST");
		        		
		        		PermissionBo p = new PermissionBo(item,"POST");
		        		list.add(p);
		        	}
	    		}
	    		final PostMapping postAnnotation = AnnotationUtils.findAnnotation(method, PostMapping.class);
	    		if (null != postAnnotation) {
	    			String values []=postAnnotation.value();
	    			String name = postAnnotation.name();
	    			for(String item : values){
	    				savePermission(name, item,"POST");
	    				
	    				PermissionBo p = new PermissionBo(item,"POST");
	    				list.add(p);
	    			}
	    		}
	    		final GetMapping getAnnotation = AnnotationUtils.findAnnotation(method, GetMapping.class);
	    		if (null != getAnnotation) {
		        	String values []=getAnnotation.value();
		        	String name = getAnnotation.name();
		        	for(String item : values){
		        		savePermission(name, item,"GET");
		        		
		        		PermissionBo p = new PermissionBo(item,"GET");
		        		list.add(p);
		        	}
	    		}
	    		final DeleteMapping deleteAnnotation = AnnotationUtils.findAnnotation(method, DeleteMapping.class);
	    		if (null != deleteAnnotation) {
	    			String values []=deleteAnnotation.value();
	    			String name = deleteAnnotation.name();
	    			for(String item : values){
	    				savePermission(name, item,"DELETE");
	    				
	    				PermissionBo p = new PermissionBo(item,"DELETE");
		        		list.add(p);
	    			}
	    		}
	    		final PutMapping putAnnotation = AnnotationUtils.findAnnotation(method, PutMapping.class);
	    		if (null != putAnnotation) {
	    			String values []=putAnnotation.value();
	    			String name = putAnnotation.name();
	    			for(String item : values){
	    				savePermission(name, item,"PUT");
	    				
	    				PermissionBo p = new PermissionBo(item,"PUT");
		        		list.add(p);
	    			}
	    		}
	    	}
	    }
	    
	    boolean exist=false;
	    for(SysPermissionVO dbPermission : dbList){
	    	exist=false;
	    	inner:for(PermissionBo bo : list){
	    		if(dbPermission.getcUrl().equals(bo.getUrl()) && dbPermission.getcMethod().equals(bo.getMethod())){
	    			exist=true;
	    			break inner;
	    		}
	    	}
	    	if(!exist){
	    		this.sysRolePermissionDao.getSysRolePermissionJpa().deleteBycPermissionId(dbPermission.getcPkId());
	    		this.sysPermissionService.getSysPermissionDao().getSysPermissionJpa().delete(dbPermission);//.deleteBycUrlAndCMethod(dbPermission.getcUrl(),dbPermission.getcMethod());
	    	}
	    }
	    
		return R.success();
	}
	
	@Data
	class PermissionBo{
		String url;
		String method;
		
		public PermissionBo(String url,String method){
			this.setUrl(url);
			this.setMethod(method);
		}
	}

	private void savePermission(String name, String item,String requestMethod) {
		SysPermissionVO bean = this.sysPermissionService.getSysPermissionDao().getSysPermissionJpa().findBycUrlAndCMethod(item, requestMethod);
		if(bean==null){
			bean = new SysPermissionVO();
			bean.setcUrl(item);
			bean.setcMethod(requestMethod);
		}
		if(!StringUtils.isBlank(name) && StringUtils.isBlank(bean.getcName())){
			bean.setcName(name);
		}
		if(StringUtils.isBlank(name) && StringUtils.isBlank(bean.getcName())){
			bean.setcName("*");
		}
		if(StringUtils.isBlank(bean.getcDescription())){
			bean.setcDescription(name);
		}
		bean.setcEnname("*");
		this.sysPermissionService.saveOrUpdate(bean);
	}
	
	//按主键查找数据对象
	@PostMapping(name="按主键查找权限表的数据",value="/api/sysPermission/detail")
	public Result detail(@RequestBody JSONObject params){
		String id = params.getString("id");
		SysPermissionVO bean = this.getSysPermissionService().findByPrimaryKey(id);
		return R.success(JSONObject.toJSON(bean));
	}
	
	//按主键删除对象
	@PostMapping(name="按主键删除权限表数据",value="/api/sysPermission/delete")
	@Transactional
	public Result delete(@RequestBody JSONObject params){
		String id = params.getString("id");
		this.getSysPermissionService().deleteByPrimaryKey(id);
		return R.success();
	}
	
	//保存或更新对象
	@PostMapping(name="保存或更新权限表",value="/api/sysPermission")
	@Transactional
	public Result saveOrUpdate(@RequestBody JSONObject params) throws Exception{
		SysPermissionVO bean = new SysPermissionVO();
		BeanUtils.map2Bean(params,bean);
		this.getSysPermissionService().saveOrUpdate(bean);
		return R.success(JSONObject.toJSON(bean));
	}
	
	//列表查询
	@PostMapping(name="权限表列表查询",value="/api/sysPermission/list")
	public Result list(@RequestBody JSONObject params) throws Exception{
		String sql = " select "+
					 "	 C_PK_ID as cPkId, "+
					 "	 C_NAME as cName, "+
					 "	 C_ENNAME as cEnname, "+
					 "	 C_URL as cUrl, "+
					 "	 C_METHOD as cMethod, "+
					 "	 C_DESCRIPTION as cDescription, "+
					 "	 date_format(T_CRT_TM,'%Y-%m-%d %H:%i:%S') as tCrtTm, "+
					 "	 date_format(T_UPD_TM,'%Y-%m-%d %H:%i:%S') as tUpdTm, "+
					 "	 C_CRT_ID as cCrtId, "+
					 "	 C_UPD_ID as cUpdId "+
					 " from "+
					 " 	 t_sys_permission"+
					 " where "+
					 "	 C_PK_ID = '#cPkId' "+
					 "	 and C_NAME = '#cName' "+
					 "	 and C_ENNAME = '#cEnname' "+
					 "	 and C_URL = '#cUrl' "+
					 "	 and C_METHOD = '#cMethod' "+
					 "	 and C_DESCRIPTION = '#cDescription' "+
					 "	 and T_CRT_TM = '#tCrtTm' "+
					 "	 and T_UPD_TM = '#tUpdTm' "+
					 "	 and C_CRT_ID = '#cCrtId' "+
					 "	 and C_UPD_ID = '#cUpdId' "+
					 " order by C_URL asc";
		PageResult pageResult = dbUtils.queryPageSql(sql,params);
		return R.success(pageResult);
	}
	
	//列表查询
	@PostMapping(name="查询所有权限列表数据，供角色选择权限时使用",value="/api/sysPermission/listAll")
	public Result listAll(@RequestBody JSONObject params) throws Exception{
		String sql = " select "+
				 "	 C_PK_ID as cPkId, "+
				 "	 C_NAME as cName, "+
				 "	 C_ENNAME as cEnname, "+
				 "	 C_URL as cUrl, "+
				 "	 C_METHOD as cMethod, "+
				 "	 C_DESCRIPTION as cDescription, "+
				 "	 '' as isChecked, "+
				 "	 date_format(T_CRT_TM,'%Y-%m-%d %H:%i:%S') as tCrtTm, "+
				 "	 date_format(T_UPD_TM,'%Y-%m-%d %H:%i:%S') as tUpdTm, "+
				 "	 C_CRT_ID as cCrtId, "+
				 "	 C_UPD_ID as cUpdId "+
				 " from "+
				 " 	 t_sys_permission"+
				 " where "+
				 "	 C_NAME like '%#cParam%' "+
				 "	 or C_ENNAME like '%#cParam%' "+
				 "	 or C_URL like '%#cParam%' "+
				 "	 or C_METHOD like '%#cParam%' "+
				 "	 or C_DESCRIPTION like '%#cParam%' "+
				 " order by C_URL asc";
		PageResult list = dbUtils.queryPageSql(sql,params);
		return R.success(list);
	}
}
