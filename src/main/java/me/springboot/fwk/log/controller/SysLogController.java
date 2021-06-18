package me.springboot.fwk.log.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import me.springboot.fwk.log.service.SysLogService;
import me.springboot.fwk.log.vo.SysLogVO;
import me.springboot.fwk.utils.BeanUtils;
import me.springboot.fwk.result.R;
import me.springboot.fwk.result.Result;
import me.springboot.fwk.db.DbUtils;
import me.springboot.fwk.db.page.PageResult;

import com.alibaba.fastjson.JSONObject;

import lombok.Getter;
import lombok.Setter;

import java.lang.String;

@RestController
@Getter
@Setter
public class SysLogController{

	@Autowired
	private SysLogService sysLogService;
	@Autowired
	private DbUtils dbUtils;
	
	//按主键查找数据对象
	@PostMapping(name="按主键查看日志详细内容",value="/api/sysLog/detail")
	public Result detail(@RequestBody JSONObject params){
		String id = params.getString("id");
		SysLogVO bean = this.getSysLogService().findByPrimaryKey(id);
		return R.success(JSONObject.toJSON(bean));
	}
	
	//按主键删除对象
	@PostMapping(name="按主键删除制定日志信息",value="/api/sysLog/delete")
	@Transactional
	public Result delete(@RequestBody JSONObject params){
		String id = params.getString("id");
		this.getSysLogService().deleteByPrimaryKey(id);
		return R.success();
	}
	
	//保存或更新对象
	@PostMapping(name="保存或更新日志信息",value="/api/sysLog")
	@Transactional
	public Result saveOrUpdate(@RequestBody JSONObject params) throws Exception{
		SysLogVO bean = new SysLogVO();
		BeanUtils.map2Bean(params,bean);
		this.getSysLogService().saveOrUpdate(bean);
		return R.success(JSONObject.toJSON(bean));
	}
	
	//列表查询
	@PostMapping(name="日志信息列表查询",value="/api/sysLog/list")
	public Result list(@RequestBody JSONObject params) throws Exception{
		String sql = " select "+
					 "	 C_PK_ID as cPkId, "+
					 "	 C_NAME as cName, "+
					 "	 C_USER_ID as cUserId, "+
					 "	 C_CLIENT_IP as cClientIp, "+
					 "	 C_SERVER_IP as cServerIp, "+
					 "	 C_SERVER_PORT as cServerPort, "+
					 "	 date_format(T_BEGIN,'%Y-%m-%d %H:%i:%S') as tBegin, "+
					 "	 date_format(T_END,'%Y-%m-%d %H:%i:%S') as tEnd, "+
					 "	 N_TOOK as nTook, "+
					 "	 C_METHOD as cMethod, "+
					 "	 C_CLASS as cClass, "+
					 "	 C_REQUEST as cRequest, "+
					 "	 C_RESPONSE as cResponse, "+
					 "	 C_URL as cUrl, "+
					 "	 C_SUCCESS as cSuccess "+
					 " from "+
					 " 	 t_sys_log"+
					 " where "+
					 "	 C_PK_ID = '#cPkId' "+
					 "	 and C_NAME = '#cName' "+
					 "	 and C_USER_ID = '#cUserId' "+
					 "	 and C_CLIENT_IP = '#cClientIp' "+
					 "	 and C_SERVER_IP = '#cServerIp' "+
					 "	 and C_SERVER_PORT = '#cServerPort' "+
					 "	 and T_BEGIN = '#tBegin' "+
					 "	 and T_END = '#tEnd' "+
					 "	 and N_TOOK = '#nTook' "+
					 "	 and C_METHOD = '#cMethod' "+
					 "	 and C_CLASS = '#cClass' "+
					 "	 and C_REQUEST = '#cRequest' "+
					 "	 and C_RESPONSE = '#cResponse' "+
					 "	 and C_URL = '#cUrl' "+
					 "	 and C_SUCCESS = '#cSuccess' "+
					 "	order by T_BEGIN desc ";
		PageResult pageResult = dbUtils.queryPageSql(sql,params);
		return R.success(pageResult);
	}
}
