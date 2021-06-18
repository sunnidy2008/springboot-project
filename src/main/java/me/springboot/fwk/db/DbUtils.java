package me.springboot.fwk.db;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;
import me.springboot.fwk.db.page.PageResult;
import me.springboot.fwk.db.page.SqlFilterUtils;
import me.springboot.fwk.exception.CustomException;

@Slf4j
@Component
public class DbUtils {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
    public DbUtils(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
	 
	
	//params的key必须含三个参数，_pageNum(请求第多少页)\_pageSize（int 每页条数）\_count(可选，默认为true,boolean:true需要总条数、false不需要总条数)
    public PageResult queryPageSql(String sql, JSONObject params)throws Exception{
        int first = 0,max=0;
        boolean count = true;

        if(!params.containsKey("_pageNum") || !params.containsKey("_pageSize")){
        	throw new CustomException(0,"分页参数_pageNum和_pageSize必选");
        }
        int _pageNum,_pageSize;
        try {
            _pageNum = Integer.parseInt(params.getString("_pageNum"));
            _pageSize = Integer.parseInt(params.getString("_pageSize"));

            if(params.containsKey("_count")){
                count = Boolean.parseBoolean(params.getString("_count"));
            }
        }catch (Exception e){
            throw new CustomException(0,"分页参数获取错误");
        }

        first = (_pageNum-1)*_pageSize;
        max = _pageSize;

        SqlFilterUtils sqlFilter = new SqlFilterUtils();
        String ql = sqlFilter.matching(sql, params);

        if (ql == null) {
            ql = sql;
            throw new Exception("SQLFilter 参数匹配只适合SELECT的语句!不适合DELETE、UPDATE操作!");
        }

        String pageSql = ql+" limit "+first+","+max;
        List resultList = this.querySql(pageSql);
        
        PageResult pageResult = new PageResult();
        pageResult.setResultList(resultList);
        if(count) {
            String countSql = "select count(*) as countX from (" + ql + ")   tmp_count";
            int total = Integer.parseInt(this.querySql(countSql).get(0).get("countX").toString());
            pageResult.setTotal(total);
        }else{
        	pageResult.setTotal(resultList.size());
        }
        return pageResult;
    }
    
	/**
	 * 查询sql（使用?作为占位符）
	 * @param sql sql查询语句
	 * @param params 参数值
	 * @return
	 */
	public List<Map<String,Object>> querySql(String sql,Object ...params){
		log.info("查询==>{}  参数==>{}",sql,params);
        List<Map<String,Object>> list = jdbcTemplate.queryForList(sql, params);
        return list;
	}
	
	
	/**
	 * 使用 :参数名 作为占位符，支持list参数
	 * @param sql
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> querySql(String sql,Map<String,Object> params){
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValues(params);
		log.info("查询==>{}  参数==>{}",sql,params);
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		List<Map<String,Object>> list = namedParameterJdbcTemplate.queryForList(sql,mapSqlParameterSource);
		return list;
	}
	
	
	/**
	 * 更新表数据（使用?作为占位符）
	 * 如：insert into t_user(username, password) values(?, ?)
	 * @param sql sql语句
	 * @param values 参数值
	 * @return
	 */
	public int updateSql(String updateSql,Object ...values){
		log.info(updateSql,values);
        return jdbcTemplate.update(updateSql, values);
	}
	
	/**
	 * 使用 :参数名 作为占位符，支持list参数
	 * delete from jo_user WHERE  1=1   AND username LIKE '%' :username '%'    
	 * :username 两边一定要有空格  
	 * @param sql
	 * @param params
	 * @return
	 */
	public int updateSql(String sql,Map<String,Object> params){
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValues(params);
		log.info("查询==>{}  参数==>{}",sql,params);
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		return namedParameterJdbcTemplate.update(sql,mapSqlParameterSource);
	}
	
//	/**
//	 * 删除表数据（使用?作为占位符）
//	 * 如：DELETE FROM t_user WHERE id = ?
//	 * @param sql sql语句
//	 * @param values 参数值
//	 * @return
//	 */
//	public int deleteSql(String deleteSql,Object ...values){
//		log.info(deleteSql,values);
//		return jdbcTemplate.update(deleteSql, values);
//	}
	
	
//	/**
//	 * 插入表数据（使用?作为占位符）
//	 * 如：insert into t_user(username, password) values(?, ?)
//	 * @param sql sql语句
//	 * @param values 参数值
//	 * @return
//	 */
//	public int insertSql(String insertSql,Object ...values){
//		log.info(insertSql,values);
//		return jdbcTemplate.update(insertSql, values);
//	}
	
}
