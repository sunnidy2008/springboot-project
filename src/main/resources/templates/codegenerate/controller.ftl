package ${package};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import ${service_package};
import ${vo_package};
import me.springboot.fwk.utils.BeanUtils;
import me.springboot.fwk.result.R;
import me.springboot.fwk.result.Result;
import me.springboot.fwk.db.DbUtils;
import me.springboot.fwk.db.page.PageResult;

import com.alibaba.fastjson.JSONObject;

import lombok.Getter;
import lombok.Setter;

<#list import_list as item>
import ${item};
</#list>

@RestController
@Getter
@Setter
public class ${class_name}{

	@Autowired
	private ${service_name} ${service_variable_name};
	@Autowired
	private DbUtils dbUtils;
	
	//按主键查找数据对象
	@PostMapping(value="/api/${model_name}/detail",name="按主键获取${table_comment}的详细记录")
	public Result detail(@RequestBody JSONObject params){
		String id = params.getString("id");
		${vo_name} bean = this.get${service_name}().findByPrimaryKey(id);
		return R.success(JSONObject.toJSON(bean));
	}
	
	//按主键删除对象
	@PostMapping(value="/api/${model_name}/delete",name="按主键删除${table_comment}的指定记录")
	@Transactional
	public Result delete(@RequestBody JSONObject params){
		String id = params.getString("id");
		this.get${service_name}().deleteByPrimaryKey(id);
		return R.success();
	}
	
	//保存或更新对象
	@PostMapping(value="/api/${model_name}",name="保存或更新数据到${table_comment}")
	@Transactional
	public Result saveOrUpdate(@RequestBody JSONObject params) throws Exception{
		${vo_name} bean = new ${vo_name}();
		BeanUtils.map2Bean(params,bean);
		this.get${service_name}().saveOrUpdate(bean);
		return R.success(JSONObject.toJSON(bean));
	}
	
	//列表查询
	@PostMapping(value="/api/${model_name}/list",name="列表查询${table_comment}的数据")
	public Result list(@RequestBody JSONObject params) throws Exception{
		String sql = " select "+
					 <#list field_list as item>
					 "	 <#if item.data_type=='datetime'>date_format(${item.column_name},'%Y-%m-%d %H:%i:%S')<#else>${item.column_name}</#if> as ${item.field_name}<#if item_has_next>,</#if> "+
					 </#list>
					 " from "+
					 " 	 ${table_name}"+
					 " where "+
					 <#list field_list as item>
					 "	 <#if item_index!=0>and </#if>${item.column_name} = '#${item.field_name}' "<#if item_has_next>+<#else>;</#if>
					 </#list>
		PageResult pageResult = dbUtils.queryPageSql(sql,params);
		return R.success(pageResult);
	}
}
