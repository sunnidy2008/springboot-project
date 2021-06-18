package ${package};

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

<#list import_list as item>
import ${item};
</#list>

import com.alibaba.fastjson.annotation.JSONField;
/**
 * ${table_comment}(${table_name})
 */
@Entity
@Table(name = "${table_name}")
public class ${vo_name} implements java.io.Serializable {

	/** 版本号 */
	private static final long serialVersionUID = ${serial_version_UID};
	
	<#list field_list as item>
	
	/** ${item.column_comment} */
	<#if item.column_key?exists && item.column_key=='PRI'>@Id</#if>
	@Column(name = "${item.column_name}" <#if item.column_key?exists && item.column_key!=''> , unique = true</#if><#if item.is_nullable?exists && item.is_nullable=='NO'> , nullable=false </#if><#if item.character_maximum_length?exists && item.data_type!='longtext'> , length = ${item.character_maximum_length}</#if>)
	private ${item.java_type} ${item.field_name};
	
	
	</#list>
	
	
	<#list field_list as item>
	@JSONField(name="${item.field_name}")
	public ${item.java_type} get${item.field_name}(){return this.${item.field_name};}
	public void set${item.field_name}(${item.java_type} ${item.field_name}){this.${item.field_name}=${item.field_name};}
	
	</#list>
	
}