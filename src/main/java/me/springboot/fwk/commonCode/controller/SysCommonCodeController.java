package me.springboot.fwk.commonCode.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import lombok.Getter;
import lombok.Setter;
import me.springboot.biz.service.OptionService;
import me.springboot.fwk.commonCode.service.SysCommonCodeService;
import me.springboot.fwk.commonCode.vo.SysCommonCodeVO;
import me.springboot.fwk.db.DbUtils;
import me.springboot.fwk.db.page.PageResult;
import me.springboot.fwk.result.R;
import me.springboot.fwk.result.Result;
import me.springboot.fwk.utils.BeanUtils;

@RestController
@Getter
@Setter
public class SysCommonCodeController{

	@Autowired
	private SysCommonCodeService sysCommonCodeService;
	@Autowired
	private OptionService optionService;
	@Autowired
	private DbUtils dbUtils;
	
	//按主键查找数据对象
	@PostMapping(name="按主键查找公共代码详细",value="/api/sysCommonCode/detail")
	public Result get(@RequestBody JSONObject params){
		String id = params.getString("id");
		SysCommonCodeVO bean = this.getSysCommonCodeService().findByPrimaryKey(id);
		return R.success(JSONObject.toJSON(bean));
	}
	
	//按主键删除对象
	@PostMapping(name="按主键删除公共代码数据",value="/api/sysCommonCode/delete")
	@Transactional
	public Result delete(@RequestBody JSONObject params){
		String id = params.getString("id");
		this.getSysCommonCodeService().deleteByPrimaryKey(id);
		return R.success();
	}
	
	//保存或更新对象
	@PostMapping(name="保存或更新公共代码",value="/api/sysCommonCode")
	@Transactional
	public Result saveOrUpdate(@RequestBody JSONObject params) throws Exception{
		SysCommonCodeVO bean = new SysCommonCodeVO();
		BeanUtils.map2Bean(params,bean);
		this.getSysCommonCodeService().saveOrUpdate(bean);
		return R.success(JSONObject.toJSON(bean));
	}
	
	//列表查询
	@PostMapping(name="公共代码列表查询",value="/api/sysCommonCode/list")
	public Result list(@RequestBody JSONObject params) throws Exception{
		String sql = " select "+
					 "	 C_PK_ID as cPkId, "+
					 "	 C_CODE as cCode, "+
					 "	 C_TEXT as cText, "+
					 "	 N_SEQ as nSeq, "+
					 "	 C_PARENT_CODE as cParentCode, "+
					 "	 C_COMMENT as cComment, "+
					 "	 C_ENABLED as cEnabled, "+
					 "	 C_CSS as cCss, "+
					 "	 date_format(T_CRT_TM,'%Y-%m-%d %H:%i:%S') as tCrtTm, "+
					 "	 date_format(T_UPD_TM,'%Y-%m-%d %H:%i:%S') as tUpdTm, "+
					 "	 C_CRT_ID as cCrtId, "+
					 "	 C_UPD_ID as cUpdId "+
					 " from "+
					 " 	 t_sys_common_code"+
					 " where "+
					 "	 C_PK_ID = '#cPkId' "+
					 "	 and C_CODE = '#cCode' "+
					 "	 and C_TEXT = '#cText' "+
					 "	 and C_PARENT_CODE = '#cParentCode' "+
					 "	 and C_COMMENT = '#cComment' "+
					 "	 and C_ENABLED = '#cEnabled' "+
					 "	 and T_CRT_TM = '#tCrtTm' "+
					 "	 and T_UPD_TM = '#tUpdTm' "+
					 "	 and C_CRT_ID = '#cCrtId' "+
					 "	 and C_UPD_ID = '#cUpdId' "+
					 "	 order by cParentCode,nSeq asc ";
		PageResult pageResult = dbUtils.queryPageSql(sql,params);
		return R.success(pageResult);
	}
	
	@PostMapping(name="获取option的text、code值",value="/api/sysCommonCode/options")
	public Result getOptions(@RequestBody JSONObject params){
		JSONObject result = new JSONObject();
		List<Map<String,String>> list = null;
		String value=null;
		for(Object key : params.keySet()){
			value = params.getString(key.toString());
			if(!StringUtils.isBlank(value) && value.startsWith("#")){
				value = value.substring(1);
				list = this.sysCommonCodeService.getSysCommonCodeDao().getSysCommonCodeJpa().get(value);
			}else{
				list = this.optionService.getOptions(value);
			}
			result.put(key.toString(), list);
		}
		return R.success(result);
	}
	
	
}
