package me.springboot.fwk.code.generate.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class CodeGenerateUtils {
	/** 
     * 生成word文件 
     * @param dataMap word中需要展示的动态数据，用map集合来保存 
     * @param templateName word模板名称，例如：test.ftl 
     * @param filePath 文件生成的目标路径，例如：D:/wordFile/ 
     * @param fileName 生成的文件名称，例如：test.doc 
     */  
    @SuppressWarnings("unchecked")  
    public static void createFile(Map dataMap,String templateName,String filePath,String fileName){  
        try {  
        	
            Configuration configuration = new Configuration();  
  
            //设置编码  
            configuration.setDefaultEncoding("UTF-8");  
  
            //ftl模板文件  
            configuration.setClassForTemplateLoading(CodeGenerateUtils.class,"/templates/codegenerate");  
  
            //获取模板  
            Template template = configuration.getTemplate(templateName);  
  
            //输出文件  
            File outFile = new File(filePath+File.separator+fileName);  
  
            //如果输出目标文件夹不存在，则创建  
            if (!outFile.getParentFile().exists()){  
                outFile.getParentFile().mkdirs();  
            }  
  
            //将模板和数据模型合并生成文件  
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile),"UTF-8"));  
  
  
            //生成文件  
            template.process(dataMap, out);  
  
            //关闭流  
            out.flush();  
            out.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
    
    /**
	 * tableName转voname
	 * @param tablename
	 * @return
	 */
	public static String tableName2VoName(String tablename){
		String keys [] = tablename.split("_");
		String result = "";
		for(String key:keys){
			result= result+key.substring(0, 1).toUpperCase() + key.substring(1).toLowerCase();
		}
		return result;
	}
	
	/**
	 * columnName转fieldName
	 * @param tablename
	 * @return
	 */
	public static String columnName2FieldName(String columnName){
		String keys [] = columnName.split("_");
		String result = "";
		for(int i=0;i<keys.length;i++){
			if(i==0){
				result = keys[i].toLowerCase();
			}else{
				result= result+keys[i].substring(0, 1).toUpperCase() + keys[i].substring(1).toLowerCase();
			}
		}
		return result;
	}
	
	/**
	 * 获取从mysql字段到java字段的映射对应关系
	 * @return
	 */
	public static Map<String,String> getColumnToFieldMapping(){
		Map<String,String> Data_Type_Mapping = new HashMap<String,String>();
		Data_Type_Mapping.put("bigint", "Integer;java.lang.Integer");
		Data_Type_Mapping.put("int", "Integer;java.lang.Integer");
		Data_Type_Mapping.put("decimal", "BigDecimal;java.math.BigDecimal");
		Data_Type_Mapping.put("varchar", "String;java.lang.String");
		Data_Type_Mapping.put("longtext", "String;java.lang.String");
		Data_Type_Mapping.put("char", "String;java.lang.String");
		Data_Type_Mapping.put("datetime", "Date;java.util.Date");
		return Data_Type_Mapping;
	}
}

