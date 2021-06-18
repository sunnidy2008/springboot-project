package me.springboot.fwk.code.generate.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import me.springboot.fwk.code.generate.utils.CodeGenerateUtils;
import me.springboot.fwk.db.DbUtils;
import me.springboot.fwk.exception.CustomException;
import me.springboot.fwk.utils.RandomUtils;

@Service
public class CodeGeneraterService {

	@Autowired
	private DbUtils dbUtils;
	
	@Value("${sys.path.codegenerate}")
	private String codegeneratePath;
	
	
	/**
	 * 获取表内字段详细内容
	 * @param tableName 表名
	 * @return
	 */
	public List<Map<String, Object>> getFieldInfoList(String tableName){
		String sql =" select "+
					"   column_name as column_name," +
					"	column_comment as column_comment," +
					"	data_type as data_type," +
					"	column_key as column_key," +
					"	character_maximum_length as character_maximum_length," +
					"	is_nullable as is_nullable," +
					"	'input' as dom_type," +
					"	'' as option_key "+
					" from "+
					"   information_schema.columns "+
					" where "+
					"   table_name=? "+
					" order by"+
					"   ordinal_position asc";
		List<Map<String, Object>> list = dbUtils.querySql(sql, tableName);
		return list;
	}
	
	/**
	 * 生成vo文件
	 * @param tableName 数据库表名（如t_sys_user）
	 * @param packageName vo文件方的package路径，以点号分割 （如me.springboot，不需要加vo，创建时会自动添加vo包)
	 * @param voName 最终生成的vo的名字（如：TbUserVO）
	 * @return 
	 * @throws Exception 
	 */
	public void generateVO(String tableName,String tableComment,String packageName,String voName,List<Map<String,Object>> fieldInfoList) throws Exception{
		
		packageName = packageName+".vo";
		
		Map<String,Object> dataMap = new HashMap<String,Object>();
		
		dataMap.put("package", packageName);
		dataMap.put("table_comment", tableComment);
		dataMap.put("table_name",tableName);
		dataMap.put("vo_name", voName);
		dataMap.put("serial_version_UID", RandomUtils.getRandom(100000000000000000L,999999999999999999L)+"L");
		
		String column_name,data_type,java_type;
		Set<String> importSet = new HashSet<String>();
		Map<String,String> columnToFieldMapping = CodeGenerateUtils.getColumnToFieldMapping();
		for(Map<String,Object> map : fieldInfoList){
			column_name = map.get("column_name").toString();
			data_type = map.get("data_type").toString();
			
			java_type = columnToFieldMapping.get(data_type.toLowerCase());
			if(StringUtils.isBlank(java_type)){
				throw new CustomException(400,"mysql数据库类型"+data_type+"无法映射到java类型，请添加CodeGenerateUtils.getColumnToFieldMapping()的映射");
			}
			map.put("java_type", java_type.split(";")[0]);
			map.put("field_name", CodeGenerateUtils.columnName2FieldName(column_name));
			importSet.add(java_type.split(";")[1]);
		}
		
		dataMap.put("field_list", fieldInfoList);
		dataMap.put("import_list", new ArrayList<String>(importSet));
		
		
		String filePath = codegeneratePath+File.separator;
		for(String item:packageName.split("\\.")){
			filePath = filePath+item+File.separator;
		}
		CodeGenerateUtils.createFile(dataMap, "vo.ftl", filePath, voName+".java");
		
	}
	
	
	/**
	 * 生成jpa
	 * @param packageName 包名（如：me.springboot，不需要加dao.jpa的包，会自动创建）
	 * @param voName 该jpa操作的是哪个vo类（如UserVO）
	 * @param primary_key_data_type 该vo类的主键字段对应的java类型（如：java.lang.String)
	 */
	public void generateJpa(String packageName,String voName,String primary_key_data_type){
		
		Map<String,Object> dataMap = new HashMap<String,Object>();
		dataMap.put("vo_package", packageName+".vo."+voName);
		packageName=packageName+".dao.jpa";
		dataMap.put("package", packageName);
		dataMap.put("vo_name", voName);
		dataMap.put("class_name", voName.substring(0, voName.length()-2)+"Jpa");//去掉VO结尾
		dataMap.put("primary_key_data_type", primary_key_data_type);
		
		String filePath = codegeneratePath+File.separator;
		for(String item:packageName.split("\\.")){
			filePath = filePath+item+File.separator;
		}
		
		CodeGenerateUtils.createFile(dataMap, "jpa.ftl", filePath, voName.substring(0, voName.length()-2)+"Jpa.java");
	}
	
	
	/**
	 * 创建dao
	 * @param packageName 包名（如：me.springboot，不需要加dao的包，会自动创建）
	 * @param voName 该dao操作的是哪个vo类（如UserVO）
	 */
	public void generateDao(String packageName,String voName){
		
		Map<String,Object> dataMap = new HashMap<String,Object>();
		dataMap.put("jpa_package", packageName+".dao.jpa."+voName.substring(0, voName.length()-2)+"Jpa");
		packageName=packageName+".dao";
		dataMap.put("package", packageName);
		dataMap.put("class_name", voName.substring(0, voName.length()-2)+"Dao");
		dataMap.put("jpa_name", voName.substring(0, voName.length()-2)+"Jpa");
		dataMap.put("jpa_variable_name", voName.substring(0, 1).toLowerCase()+voName.substring(1, voName.length()-2)+"Jpa");
		
		String filePath = codegeneratePath+File.separator;
		for(String item:packageName.split("\\.")){
			filePath = filePath+item+File.separator;
		}
		
		CodeGenerateUtils.createFile(dataMap, "dao.ftl", filePath, voName.substring(0, voName.length()-2)+"Dao.java");
	}
	
	/**
	 * 创建dao
	 * @param packageName 包名（如：me.springboot，不需要加dao的包，会自动创建）
	 * @param voName 该service操作的是哪个vo类（如UserVO）
	 * @param primary_key_data_type 该vo类的主键字段对应的java类型（如：java.lang.String)
	 * @param primary_key 该vo类的主键字段对应的java字段
	 */
	public void generateService(String packageName,String voName,String primary_key_data_type,String primary_key){
		
		Map<String,Object> dataMap = new HashMap<String,Object>();
		dataMap.put("dao_package", packageName+".dao."+voName.substring(0, voName.length()-2)+"Dao");
		dataMap.put("vo_package", packageName+".vo."+voName);
		dataMap.put("vo_name", voName);
		packageName=packageName+".service";
		dataMap.put("package", packageName);
		dataMap.put("class_name", voName.substring(0, voName.length()-2)+"Service");
		dataMap.put("jpa_name", voName.substring(0, voName.length()-2)+"Jpa");
		dataMap.put("dao_name", voName.substring(0, voName.length()-2)+"Dao");
		dataMap.put("dao_variable_name", voName.substring(0,1).toLowerCase()+voName.substring(1, voName.length()-2)+"Dao");
		dataMap.put("primary_key_data_type", primary_key_data_type.substring(primary_key_data_type.lastIndexOf(".")+1));
		dataMap.put("primary_key", primary_key);
		
		List<String> import_list = new ArrayList<String>();
		import_list.add(primary_key_data_type);
		dataMap.put("import_list", import_list);
		
		String filePath = codegeneratePath+File.separator;
		for(String item:packageName.split("\\.")){
			filePath = filePath+item+File.separator;
		}
		
		CodeGenerateUtils.createFile(dataMap, "service.ftl", filePath, voName.substring(0, voName.length()-2)+"Service.java");
	}
	
	
	/**
	 * 创建controller 
	 * @param packageName 包名（如：me.springboot，不需要加dao的包，会自动创建）
	 * @param voName 该controller操作的是哪个vo类（如UserVO）
	 * @param primary_key_data_type 该vo类的主键字段对应的java类型（如：java.lang.String)
	 */
	public void generateController(String packageName,String voName,String table_name,String table_comment,String primary_key_data_type,List<Map<String,Object>> fieldInfoList){
		
		Map<String,Object> dataMap = new HashMap<String,Object>();
		dataMap.put("table_name", table_name);
		dataMap.put("table_comment", table_comment);
		dataMap.put("service_package", packageName+".service."+voName.substring(0, voName.length()-2)+"Service");
		dataMap.put("vo_package", packageName+".vo."+voName);
		dataMap.put("vo_name", voName);
		packageName=packageName+".controller";
		dataMap.put("package", packageName);
		dataMap.put("class_name", voName.substring(0, voName.length()-2)+"Controller");
		dataMap.put("service_name", voName.substring(0, voName.length()-2)+"Service");
		dataMap.put("service_variable_name", voName.substring(0,1).toLowerCase()+voName.substring(1, voName.length()-2)+"Service");
		dataMap.put("primary_key_data_type", primary_key_data_type.substring(primary_key_data_type.lastIndexOf(".")+1));
		dataMap.put("model_name", voName.substring(0,1).toLowerCase()+voName.substring(1, voName.length()-2));
		
		List<String> import_list = new ArrayList<String>();
		import_list.add(primary_key_data_type);
		dataMap.put("import_list", import_list);
		
		String filePath = codegeneratePath+File.separator;
		for(String item:packageName.split("\\.")){
			filePath = filePath+item+File.separator;
		}
		
		for(Map map :fieldInfoList){
			map.put("field_name", CodeGenerateUtils.columnName2FieldName(map.get("column_name").toString()));
		}
		dataMap.put("field_list", fieldInfoList);

		CodeGenerateUtils.createFile(dataMap, "controller.ftl", filePath, voName.substring(0, voName.length()-2)+"Controller.java");
	}
	
	
	/**
	 * 生成html_list
	 * @param voName
	 * @param table_name
	 * @param tableComment
	 */
	public void generateHtmlList(String voName,String table_name,String tableComment,List<Map<String,Object>> fieldInfoList){
		String model_name = voName.substring(0,1).toLowerCase()+voName.substring(1, voName.length()-2);
		
		Map<String,Object> dataMap = new HashMap<String,Object>();
		dataMap.put("title", tableComment);
		
		dataMap.put("model_name", model_name);
		
		
		String filePath = codegeneratePath+File.separator+"html"+File.separator+model_name+File.separator;
		
		for(Map map :fieldInfoList){
			map.put("field_name", CodeGenerateUtils.columnName2FieldName(map.get("column_name").toString()));
			
			if("PRI".equals(map.get("column_key"))){
				dataMap.put("primary_key", map.get("field_name"));
			}
		}
		dataMap.put("field_list", fieldInfoList);
		
		CodeGenerateUtils.createFile(dataMap, "html_list.ftl", filePath, model_name+"List.html");
	}
	
	/**
	 * 生成html_detail
	 * @param voName
	 * @param table_name
	 * @param tableComment
	 * @param fieldInfoList 表字段描述
	 * @throws Exception 
	 */
	public void generateHtmlDetail(String voName,String table_name,String tableComment,List<Map<String,Object>> fieldInfoList) throws Exception{
		String model_name = voName.substring(0,1).toLowerCase()+voName.substring(1, voName.length()-2);
		
		Map<String,Object> dataMap = new HashMap<String,Object>();
		dataMap.put("title", tableComment);
		
		dataMap.put("model_name", model_name);
		
		
		String filePath = codegeneratePath+File.separator+"html"+File.separator+model_name+File.separator;
		
		for(Map map :fieldInfoList){
			map.put("field_name", CodeGenerateUtils.columnName2FieldName(map.get("column_name").toString()));
			
			if("PRI".equals(map.get("column_key"))){
				dataMap.put("primary_key", map.get("field_name"));
			}
		}
		if(!dataMap.containsKey("primary_key")){
			throw new Exception(table_name+"没有主键，html_list生成失败");
		}
		dataMap.put("field_list", fieldInfoList);
		
		CodeGenerateUtils.createFile(dataMap, "html_detail.ftl", filePath, model_name+"Detail.html");
	}
	
	
	
	
	
	public static void main(String args[]) throws Exception{
		CodeGeneraterService s = new CodeGeneraterService();
		s.generateVO("t_sys_user","用户表", "com.springboot.vo", "UserVO.java",s.getFieldInfoList("t_user"));
	}
}
