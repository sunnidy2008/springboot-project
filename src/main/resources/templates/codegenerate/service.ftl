package ${package};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ${dao_package};
import ${vo_package};
import java.util.Date;
import java.util.UUID;
import me.springboot.fwk.utils.VoUtils;
import me.springboot.fwk.sys.UserUtils;

import lombok.Getter;
import lombok.Setter;

<#list import_list as item>
import ${item};
</#list>

@Service
@Getter
@Setter
public class ${class_name}{

	@Autowired
	private ${dao_name} ${dao_variable_name};
	
	//按主键查找数据对象
	public ${vo_name} findByPrimaryKey(${primary_key_data_type} primaryKey){
		return this.get${dao_name}().get${jpa_name}().findById(primaryKey).get();
	}
	
	//按主键删除对象
	public void deleteByPrimaryKey(${primary_key_data_type} primaryKey){
		this.get${dao_name}().get${jpa_name}().deleteById(primaryKey);
	}
	
	//保存或更新对象
	public void saveOrUpdate(${vo_name} bean){
		VoUtils.touch(bean,new Date(),UserUtils.getUser().getId());
		if(bean.get${primary_key}()==null){
			bean.set${primary_key}(UUID.randomUUID().toString());
		}
		this.get${dao_name}().get${jpa_name}().save(bean);
	}
}
