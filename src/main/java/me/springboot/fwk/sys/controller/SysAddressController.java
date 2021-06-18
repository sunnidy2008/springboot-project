package me.springboot.fwk.sys.controller;

import java.util.HashMap;
import java.util.Map;

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
import me.springboot.fwk.sys.service.SysAddressService;
import me.springboot.fwk.sys.vo.SysAddressVO;
import me.springboot.fwk.utils.BeanUtils;

@RestController
@Getter
@Setter
public class SysAddressController{

	@Autowired
	private SysAddressService sysAddressService;
	@Autowired
	private DbUtils dbUtils;
	
	//按主键查找数据对象
	@PostMapping(value="/api/sysAddress/detail",name="按主键获取省市县区街道基础数据的详细记录")
	public Result detail(@RequestBody JSONObject params){
		String id = params.getString("id");
		SysAddressVO bean = this.getSysAddressService().findByPrimaryKey(id);
		return R.success(JSONObject.toJSON(bean));
	}
	
	//按主键删除对象
	@PostMapping(value="/api/sysAddress/delete",name="按主键删除省市县区街道基础数据的指定记录")
	@Transactional
	public Result delete(@RequestBody JSONObject params){
		String id = params.getString("id");
		this.getSysAddressService().deleteByPrimaryKey(id);
		
		String sql="delete from t_sys_address where c_parent like :parent '%'";
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("parent", id);
		dbUtils.updateSql(sql, map);
		
		return R.success();
	}
	
	//保存或更新对象
	@PostMapping(value="/api/sysAddress",name="保存或更新数据到省市县区街道基础数据")
	@Transactional
	public Result saveOrUpdate(@RequestBody JSONObject params) throws Exception{
		SysAddressVO bean = new SysAddressVO();
		BeanUtils.map2Bean(params,bean);
		this.getSysAddressService().saveOrUpdate(bean);
		return R.success(JSONObject.toJSON(bean));
	}
	
	//列表查询
	@PostMapping(value="/api/sysAddress/list",name="列表查询省市县区街道基础数据的数据")
	public Result list(@RequestBody JSONObject params) throws Exception{
		params.put("_pageSize",1000);
		params.put("_pageNum",1);
		params.put("_count",false);
		
		String sql = " select "+
					 "	 C_CODE as cCode, "+
					 "	 C_NAME as cName, "+
					 "	 C_PARENT as cParent, "+
					 "	 C_TYPE as cType "+
					 " from "+
					 " 	 t_sys_address"+
					 " where "+
					 "	 C_CODE = '#cCode' "+
					 "	 and C_NAME = '#cName' "+
					 "	 and C_PARENT = '#cParent' "+
					 "	 and C_TYPE = '#cType' "+
					 " order by C_CODE asc ";
		PageResult pageResult = dbUtils.queryPageSql(sql,params);
		return R.success(pageResult);
	}
}
