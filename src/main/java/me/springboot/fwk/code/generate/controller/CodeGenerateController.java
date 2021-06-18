package me.springboot.fwk.code.generate.controller;

import java.io.File;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import me.springboot.fwk.code.generate.service.CodeGeneraterService;
import me.springboot.fwk.code.generate.utils.CodeGenerateUtils;
import me.springboot.fwk.db.DbUtils;
import me.springboot.fwk.result.R;
import me.springboot.fwk.result.Result;
import me.springboot.fwk.utils.ZipUtils;

@RestController
public class CodeGenerateController {

	@Autowired
	private DbUtils dbUtils;
	@Autowired
	private CodeGeneraterService codeGeneraterService;
	@Value("${sys.path.codegenerate}")
	private String codegeneratePath;
	@Value("${sys.tableSchema}")
	private String tableSchema;
	
	/**
	 * 获取数据库所有的表
	 * @return
	 */
	@PostMapping(value="/api/code/generate/tablenames",name="获取数据库所有的表")
	public Result getTableNames(@RequestBody JSONObject params){
		String sql = "select table_name as tableName,table_comment as tableComment,'false' as isChecked from information_schema.tables where table_type='BASE TABLE' and table_schema='"+tableSchema+"'";
		if(params.get("tableName")!=null && !"".equals(params.getString("tableName"))){
			sql = sql+" and table_name like '%"+params.getString("tableName")+"%'";
		}
		sql = sql+" order by table_name desc";
		
		List<Map<String, Object>> list = dbUtils.querySql(sql);
		if(list==null || list.size()==0){
			return R.success(list);
		}
		String name ;
		for(Map map : list){
			name = map.get("tableName").toString();
			map.put("voName", CodeGenerateUtils.tableName2VoName(name.substring(name.indexOf("_")+1))+"VO");//将下划线转驼峰
		}
		return R.success(list);
	}
	
	@PostMapping(name="获取制定表的所有字段信息",value="/api/code/generate/columnInfo")
	public Result getColumnInfo(@RequestBody JSONObject params){
		String tableName = params.getString("tableName");
		List<Map<String, Object>> fieldInfoList = codeGeneraterService.getFieldInfoList(tableName);
		for(Map<String,Object> map : fieldInfoList){
			if("datetime".equals(map.get("data_type")) ||"date".equals(map.get("data_type"))){
				map.put("dom_type", "datetime");
			}else{
				map.put("dom_type", "input");
			}
		}

		return R.success(fieldInfoList);
	}
	
	/**
	 * 生成代码
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@PostMapping(name="生成java和html代码",value="/api/code/generate",produces =  "application/octet-stream;charset=UTF-8")
//	public Result generat(@RequestBody JSONObject params) throws Exception{
	public void generat(HttpServletRequest request, HttpServletResponse response) throws Exception{
		//删除文件及文件夹
		deleteDir(new File(codegeneratePath));
		//重建代码生成目录
    	File codegenerateFolder = new File(codegeneratePath);
		if(!codegenerateFolder.exists()){
			codegenerateFolder.mkdir();
		}
		
		
//		String packageName = params.getString("packageName");
//		JSONArray ja = params.getJSONArray("datas");
		String packageName = request.getParameter("packageName");
		JSONArray ja = JSONArray.parseArray(request.getParameter("datas"));
		
		String tableName,voName,tableComment,primary_key="",primary_key_data_type="";
		JSONArray columnInfo;
		JSONObject columnJson;
		Boolean isChecked;
		Map<String,String> mapping = CodeGenerateUtils.getColumnToFieldMapping();
		for(int i=0;i<ja.size();i++){
			JSONObject tmp = ja.getJSONObject(i);
			tableName = tmp.getString("tableName");
			tableComment = tmp.getString("tableComment");
			voName = tmp.getString("voName");
			isChecked = tmp.getBoolean("isChecked");
			
			if(isChecked){
				List<Map<String, Object>> fieldInfoList = codeGeneraterService.getFieldInfoList(tableName);
				if(tmp.containsKey("columnInfo")){
					columnInfo = tmp.getJSONArray("columnInfo");
					for(int j=0;j<columnInfo.size();j++){
						columnJson = columnInfo.getJSONObject(j);
						for(Map<String,Object> map:fieldInfoList){
							if(columnJson.getString("column_name").equals(map.get("column_name").toString())){
								map.put("dom_type", columnJson.getString("dom_type"));
								map.put("option_key", columnJson.getString("option_key"));
							}
						}
					}
				}
				
				for(Map<String,Object> map : fieldInfoList){
					if("PRI".equals(map.get("column_key").toString())){
						primary_key = CodeGenerateUtils.columnName2FieldName(map.get("column_name").toString());
						if(!mapping.containsKey(map.get("data_type").toString())){
							throw new Exception("mysql数据库中表"+tableName+" 字段"+map.get("column_name")+" 类型"+map.get("data_type")+" 无法映射到java类型");
						}
						primary_key_data_type=mapping.get(map.get("data_type").toString()).split(";")[1];
						break;
					}
				}
				
				codeGeneraterService.generateVO(tableName, tableComment, packageName, voName,fieldInfoList);
				codeGeneraterService.generateJpa(packageName, voName, primary_key_data_type);
				codeGeneraterService.generateDao(packageName, voName);
				codeGeneraterService.generateService(packageName, voName,primary_key_data_type,primary_key);
				codeGeneraterService.generateController(packageName, voName,tableName,tableComment,primary_key_data_type,fieldInfoList);
				codeGeneraterService.generateHtmlList(voName, tableName, tableComment,fieldInfoList);
				codeGeneraterService.generateHtmlDetail(voName, tableName, tableComment,fieldInfoList);
			}
		}
//		String tableName = "t_sys_log";
//		String tableComment = "日志";
//		String packageName ="me.springboot.test";
//		String voName = "SysLogVO";
//		List<Map<String, Object>> fieldInfoList = codeGeneraterService.getFieldInfoList(tableName);
//		String primary_key_data_type = codeGeneraterService.generateVO(tableName, tableComment, packageName, voName,fieldInfoList);
//		codeGeneraterService.generateJpa(packageName, voName, primary_key_data_type);
//		codeGeneraterService.generateDao(packageName, voName);
//		codeGeneraterService.generateService(packageName, voName,primary_key_data_type);
//		codeGeneraterService.generateController(packageName, voName,tableName,primary_key_data_type,fieldInfoList);
//		codeGeneraterService.generateHtmlList(voName, tableName, tableComment,fieldInfoList);
//		codeGeneraterService.generateHtmlDetail(voName, tableName, tableComment,fieldInfoList);
		exportZip(codegenerateFolder);
//		return R.success();
	}
	
	
	//将生成的代码文件打包
	private void exportZip(File tempFolder) throws Exception{
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = servletRequestAttributes.getRequest();
		HttpServletResponse response = servletRequestAttributes.getResponse();
		response.setCharacterEncoding(request.getCharacterEncoding());
		
		String downloadName = URLEncoder.encode("code.zip", "UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + downloadName);
    	response.setContentType("application/octet-stream");
    	
    	OutputStream outputStream = response.getOutputStream();
		ZipUtils.toZip(tempFolder.getPath(), outputStream, true);
		
		outputStream.flush();
        outputStream.close();
	}
	
	
	//循环删除目录文件
	private void deleteDir(File dir){
		if(dir.isDirectory()){
			File files[] = dir.listFiles();
			for(File file : files){
				if(file.isDirectory()){
					deleteDir(file);
				}else{
					file.delete();
				}
			}
			dir.delete();
		}else {
			dir.delete();
		}
	}
	
	
	
	
	
	
	
	
	
	
}
