package me.springboot.fwk.db.page;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;


/**
 * 功能：SQL语句解析器类
 */
@Slf4j
public class SqlFilterUtils{
	private final static String  PARAM_REGEX="(#\\w+\\.{0,1}\\w+)";
	//private final static String  FIELD_REGEX="(\\s*select\\s{1}|\\s{1}from\\s{1}|\\s{1}where\\s{1}|\\s{1}and\\s{1}|\\s{1}or\\s{1}|\\s{0,1}\\(\\s{0,1}|\\s{0,1}\\)\\s{0,1}|\\s{1}order\\s+by\\s{1}|\\s{1}group\\s+by\\s{1}|\\s{1}union\\s+\\all\\s{1})";
	//Ferris.Deng 2011-01-11添加了with语句后参数空值问题
	//Ferris.Deng 2011-03-24 添加了右括号的分割，当需要使用右括号进行分割时，右括号前必须要有一个空格，否则无法分割
	//而如果需要解析成参数键值对的右括号之前不能带有空格，否则会被当做分隔符解析
	private final static String  FIELD_REGEX="(\\s{1}with\\s{1}|\\s*select\\s{1}|\\s{1}from\\s{1}|\\s{1}where\\s{1}|\\s{1}and\\s{1}|\\s{1}or\\s{1}|\\s{1}\\)|\\s{1}order\\s+by\\s{1}|\\s{1}group\\s+by\\s{1}|\\s{1}union\\s+all\\s{1})|\\s{1}union\\s{1}|\\s{1}minus\\s{1}|\\)\\s+as\\s+";

	public SqlFilterUtils(){    	
	}
    
	/**
	 * 功能:得到指定SQL中的 :XXX参数定义
	 * @param sql 要格式化的SQL
	 * @param regex  获取:XXX参数的regex
	 * @return  :XXX的集合 如:  [:CDptCde, :CDptCnm]
	 */
	public List<String> getParameters(String sql,String regex){
		ArrayList<String> lstParam =new ArrayList<String>(); 
		if(regex==null) regex=SqlFilterUtils.PARAM_REGEX;//"(:\\w+)";
		Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
	    // 用Pattern类的matcher()方法生成一个Matcher对象 
	    Matcher matcher = pattern.matcher(sql);                
	    // 使用find()方法查找第一个匹配的对象
	    boolean result = matcher.find(); 
	    
	    while (result) {
	    	String matcherVal = matcher.group(0);
	    	if(!":%i:%s".equals(matcherVal) && !":%i".equals(matcherVal) && !":%s".equals(matcherVal))
	    		lstParam.add(matcher.group(0));
	    	        	      
	        result = matcher.find();
	    }
	    return lstParam;     	
	}
	
	public static void main1(String args[]){
		
		Pattern pattern = Pattern.compile(SqlFilterUtils.FIELD_REGEX, Pattern.CASE_INSENSITIVE);
		// 用Pattern类的matcher()方法生成一个Matcher对象
		String sql = "select       a.c_pk_id as pkId,      (select c_user_nme from t_user x where x.c_user_id=a.c_user_id) as doctorNme,       b.c_dpt_nme as dptNme,      (select c_office_nme from t_bas_office y where y.c_office_id=c.c_office_id) as officeNme,      date_format(a.t_date,'%Y-%m-%d') as date,      a.n_max_num1 as maxNum1,      a.n_num1 as num1,      a.n_max_num2 as maxNum2,      a.n_num2 as num2 from       t_work_sched a,t_bas_dpt b,t_doctor_auth c  where        (c.c_dpt_id='#CDptId' or c.c_dpt_id1='#CDptId' or c.c_dpt_id2='#CDptId')        and a.t_date<='#TEndTm' and a.t_date>='#TBgnTm'        and b.c_province='#CPro' and b.c_city='#CCity'        and a.c_dpt_id=b.c_dpt_id        and a.c_user_id=c.c_user_id        and (a.c_dpt_id=c.c_dpt_id or a.c_dpt_id=c.c_dpt_id1 or a.c_dpt_id=c.c_dpt_id2) order by        a.c_dpt_id,a.c_user_id,a.t_date desc";
		Matcher matcher = pattern.matcher(sql);
		
		boolean result = matcher.find();
		StringBuffer sb = new StringBuffer();
		
		while(result){
			matcher.appendReplacement(sb, "");
			String forum = sb.toString();
			if (forum.indexOf("#") > -1) {
			}
			result = matcher.find();
		}
	}
	/**
	 * 
	 * 功能:得到包含 :XXX的表达式
	 * @param sql 要格式化的SQL
	 * @param regex 
	 * @return  包含 :XXX的表达式的集合 如[a.CDptCde =  ':CDptCde' , a.CDptCnm =  ':CDptCnm']
	 */
	public List<String> getField(String sql, String regex) {
		ArrayList<String> lstField = new ArrayList<String>();
		if (regex == null) {
			regex = SqlFilterUtils.FIELD_REGEX;
		}
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		// 用Pattern类的matcher()方法生成一个Matcher对象
		Matcher matcher = pattern.matcher(sql);
		StringBuffer sb = new StringBuffer();
		// 使用find()方法查找第一个匹配的对象
		boolean result = matcher.find();
		// 使用循环找出模式匹配的内容替换之,再将内容加到sb里
		int i = 0;
		while (result) {
			matcher.appendReplacement(sb, "");
			String forum = sb.toString();
			if (sb.length() > 0) {
				if (forum.indexOf("#") > -1) {
					// Ferris.Deng2011-01-11添加了with语句后出现空值参数的问题，删除最后的单独的")"
					if (forum.lastIndexOf(')') > 0
							&& forum.lastIndexOf('(') < 0) {
						forum = forum.replaceAll("\\)", "");
					}
					//yaos 修复 （field='#f1' or filed2='#f2'）时出错，前面的（会被带进来
					if (forum.lastIndexOf('(') > -1
							&& forum.lastIndexOf(')') < 0) {
						forum = forum.replaceAll("\\(", "");
					}
					
					lstField.add(forum);
					/*
					//修复问题当sql="select * from (select * from tableA where c_lev_cde = ':CLevCde' )  a1, (select * from tableB) b1"时，
					//此时解析出来的参数名值对为:c_lev_cde = ':CLevCde')  a1, ( 
					
					//判断是否有单引号
					if(forum.lastIndexOf("(")!=-1) {
						forum = forum.substring(0, forum.lastIndexOf(")"));
						lstField.add(forum+")");
					} else if(forum.lastIndexOf("'")!=-1) {
						forum = forum.substring(0, forum.lastIndexOf("'"));
						lstField.add(forum+"'");
					} else {
						lstField.add(forum);
					}
					*/
				}
			}
			if (i == 0) {// 只限制select 操作
				String startTag = forum.trim().toLowerCase();
				if (startTag.startsWith("delete") || startTag.startsWith("update")) {
					return null;
				}
				i++;
			}
			sb.setLength(0);
			result = matcher.find();
		}
		// 最后调用appendTail()方法将最后一次匹配后的剩余字符串加到sb里；
		matcher.appendTail(sb);
		String forum = sb.toString();
		if (forum.indexOf("#") > -1)
			lstField.add(forum);
		return lstField;

	}
    /**
     * 参数匹配，将sql中存在的变量，用参数队列对应的值匹配，对于参数队列中不存在的变量，从sql语句中替换为 1=1
      * @param sql String  sql语句
      * @param mapParam Map
      * @return String 新的sql
      * @throws Exception
      */   
    public String matching(String sql,Map<String,Object> mapParam) throws Exception{
    	String newSql=sql;
    	List<String> lstField = this.getField(sql,null);  
    	log.debug("lstField="+lstField);
    	if(lstField == null )
    		return null;
    	List<String> lstParam =this.getParameters(sql,null);
    	log.debug("lstParam="+lstParam);
        for(int i=0;i<lstParam.size();i++){        	
        	String keyParam = lstParam.get(i);
        	//System.err.println("keyParam="+keyParam+"*");
        	String newKey =keyParam.substring(1);
        	//System.err.println("newKey="+newKey+"*");        	
        	Object inputVal =mapParam.get(newKey); 
        	//System.err.println("inputVal 0="+inputVal);
        	if(inputVal!=null&&!"".equals(inputVal)){
        		//System.err.println("inputVal="+inputVal.toString());
        		if(inputVal instanceof List){//in 条件处理
        		  List lstInv =(List)inputVal;
        		  int paramIndex = sql.indexOf("'"+keyParam+"'");
        		  StringBuffer sInputValues =new StringBuffer();
        		  for(int pk=0;pk<lstInv.size();pk++){
        			  if(pk==0){
        				  if(paramIndex>-1 && pk>0){
        				    sInputValues.append("'");
        				  }
        				  sInputValues.append(lstInv.get(pk));
        				  if(paramIndex>-1&& lstInv.size()>1){
        				    sInputValues.append("'");
        				  }
        			  }else{
        				  sInputValues.append(",");
        				  if(paramIndex>-1){
        				    sInputValues.append("'");
        				  }
        				  sInputValues.append(lstInv.get(pk));
        				  if(paramIndex>-1 && (pk<lstInv.size()-1)){
        				    sInputValues.append("'");
        				  }
        			  }
        		  }
        		  newSql=newSql.replaceAll(keyParam,sInputValues.toString());
        		}else{
        		  if(inputVal instanceof Long){
        			  newSql=newSql.replaceAll(keyParam,((Long)inputVal).longValue()+"");   
        		  }else if(inputVal instanceof Double){
        			  newSql=newSql.replaceAll(keyParam,((Double)inputVal).doubleValue()+"");  
        		  }else if(inputVal instanceof Integer){
        			  newSql=newSql.replaceAll(keyParam,((Integer)inputVal).intValue()+"");  
        		  }else if(inputVal instanceof Float){
        			  newSql=newSql.replaceAll(keyParam,((Float)inputVal).floatValue()+""); 
        		  }else if(inputVal instanceof Number){
        			  newSql=newSql.replaceAll(keyParam,((Number)inputVal).doubleValue()+"");
        		  }else{
        			  newSql=newSql.replaceAll(keyParam,(String)inputVal);  
        		  }
        		}
        	}else{//inputVal==null
        		String tmpReg  = lstField.get(i).replace("(", "\\(");
        		tmpReg =tmpReg.replace(")", "\\)");
        		tmpReg =tmpReg.replace("+", "\\+");
        		tmpReg =tmpReg.replace("*", "\\*");
        		tmpReg =tmpReg.replace("|", "\\|");
        		//yaos
        		newSql=newSql.replaceAll(tmpReg.trim(), " 1=1 ");
//        		newSql=newSql.replaceAll(tmpReg, " 1=1 ");
        	}
        }
    	return newSql;
    }
    
    public static void main(String[] args) throws Exception {
//    	SqlFilterUtils sqlFilter = new SqlFilterUtils();
//    	/*
//    	StringBuilder sqlSb = new StringBuilder();
//    	sqlSb.append("select t.c_cha_mrk as CChaMrk, t.c_cha_cde as CChaCde, t.c_cha_nme as CChaNme, " +
//    	"t.c_dpt_cde as CDptCde, t.c_cha_cls as CBsnsTyp, t.c_cha_type as CChaType, t.c_cha_subtype as CChaSubtype from web_cus_cha t ")
//    	.append("where t.c_cha_mrk = ':CChaMrk' ")
//    	.append("and t.c_cha_cde = ':CChaCde' ")
//    	.append("and t.c_cha_cls = ':CBsnsTyp' ")
//    	.append("and t.c_cha_type = ':CChaType' ")
//    	.append("and t.c_cha_subtype = ':CChaSubtype' ")
//    	.append("and t.c_cha_nme = ':CChaNme%' ")
//    	.append("and contains( a.C_DPT_REL_CDE,':CDptCde' )>0 ")
//    	.append("and t.c_dpt_cde in ( ")
//    	.append("select a.c_dpt_cde from web_org_dpt a connect by prior a.c_dpt_cde = a.c_snr_dpt start with a.c_dpt_cde = ':CDptCde' ")
//    	.append(") and a.c_dpt_cde = ':CDptCde'");
//
//    	HashMap<String, Object> map = new HashMap<String, Object>();
//    	map.put("CChaMrk", "CChaMrk");
//    	map.put("CChaCde", "CChaCde");
//    	map.put("CBsnsTyp", "CBsnsTyp");
//    	map.put("CChaType", "CChaType");
//    	map.put("CChaSubtype", "CChaSubtype");
//    	map.put("CChaNme", "CChaNme");
//    	//map.put("CDptCde", "CDptCde");
//    	String ql = sqlFilter.matching(sqlSb.toString(), map);
//    	*/
////    	String sql = "SELECT new map(base.TUdrTm as TUdrTm, prod.CNmeCn as CProdNmeCn,  " +
////    			"  vhl.CPlateNo as CPlateNo)  FROM PlyBaseVO base, AppApplicantVO applicant, AppVhlVO vhl, PrdProdVO prod, OrgDptVO dpt " +
////    			" WHERE base.CAppNo = vhl.CAppNo  AND base.CAppNo = applicant.CAppNo  AND base.CProdNo = prod.CProdNo  " +
////    			"AND base.CDptCde = dpt.CDptCde  AND base.CAppTyp = 'A'  AND base.CPlyNo IS NOT NULL  " +
////    			"AND applicant.CAppNme like decode(':CAppNme%')  AND base.CCommonFlag in (':CCommonFlag')" +
////    			"  AND vhl.CPlateNo = ':CPlateNo'  AND base.TAppTm >= to_date(':TAppTmStart', 'yyyy-mm-dd')" +
////    			"  AND base.TAppTm < to_date(':TAppTmEnd', 'yyyy-mm-dd')+1  " +
////    			"AND base.TInsrncBgnTm >= to_date(':TInsrncBgnTmStart', 'yyyy-mm-dd')  " +
////    			"AND base.TInsrncBgnTm < to_date(':TInsrncBgnTmEnd', 'yyyy-mm-dd')+1  " +
////    			"AND EXISTS ( SELECT CDptRelCde FROM OrgDptVO WHERE CDptCde=':CDptCde' or dpt.CDptRelCde like concat(CDptRelCde,'%') )" +
////    			"  AND base.CProdNo in (':CProdNo')  ORDER BY base.TAppTm DESC";
//    	String sql = "SELECT new map(base.TUdrTm as TUdrTm, prod.CNmeCn as CProdNmeCn,  " +
//		"  vhl.CPlateNo as CPlateNo)  FROM PlyBaseVO base, AppApplicantVO applicant, AppVhlVO vhl, PrdProdVO prod, OrgDptVO dpt " +
//		" WHERE base.CAppNo = vhl.CAppNo  AND base.CAppNo = applicant.CAppNo  AND base.CProdNo = prod.CProdNo  " +
//		"AND base.CDptCde = dpt.CDptCde  AND base.CAppTyp = 'A'  AND base.CPlyNo IS NOT NULL  " +
//		"AND applicant.CAppNme like decode('#CAppNme%')  AND base.CCommonFlag in ('#CCommonFlag')" +
//		"  AND vhl.CPlateNo = '#CPlateNo'  AND base.TAppTm >= to_date('#TAppTmStart', 'yyyy-mm-dd')" +
//		"  AND base.TAppTm < to_date('#TAppTmEnd', 'yyyy-mm-dd')+1  " +
//		"AND base.TInsrncBgnTm >= to_date('#TInsrncBgnTmStart', 'yyyy-mm-dd')  " +
//		"AND base.TInsrncBgnTm < to_date('#TInsrncBgnTmEnd', 'yyyy-mm-dd')+1  " +
//		"AND EXISTS ( SELECT CDptRelCde FROM OrgDptVO WHERE CDptCde='#CDptCde' or dpt.CDptRelCde like concat(CDptRelCde,'%') )" +
//		"  AND base.CProdNo in ('#CProdNo')  ORDER BY base.TAppTm DESC";
//
////    	String hql ="select abc as a from t where t=':a'"
//    	/*
//    	String sql = "select distinct B.c_cha_cls as CBsnsTyp, B.c_cha_cde as CChaCde, B.c_cha_nme as CChaNme,  B.c_dpt_cde as CDptCde, B.c_cha_type as CChaType, B.c_cha_subtype as CChaSubtype, DPT.C_DPT_CNM AS CAgtAgrItemCde, A.c_agt_agr_no AS CAgtAgrNo FROM WEB_CUS_CONFER A, WEB_CUS_CHA B, WEB_CUS_CONFER_DTL C,web_org_dpt dpt  WHERE  A.C_CLNT_CDE = B.C_CHA_CDE  AND A.C_AGT_AGR_NO = C.C_AGT_AGR_NO  AND dpt.c_dpt_cde = A.c_dpt_cde AND B.c_cha_mrk = ':CChaMrk'  AND B.c_cha_cde = ':CChaCde'  AND B.c_cha_cls = ':CBsnsTyp'  AND B.c_cha_type = ':CChaType'  AND B.c_cha_subtype = ':CChaSubtype'  AND B.c_cha_nme like ':CChaNme%'  and A.t_co_end_tm >  to_date('123','yyyy-mm-dd hh24:mi:ss')  AND EXISTS ( select t.c_dpt_rel_cde from web_org_dpt t where dpt.C_Dpt_Rel_Cde like concat(C_Dpt_Rel_Cde,'%')  AND t.c_dpt_cde = ':CDptCde' )";
//    	HashMap<String, Object> map = new HashMap<String, Object>();
//    	map.put("CBsnsTyp", "CBsnsTyp");
//    	map.put("CChaMrk", "CChaMrk");
//    	map.put("CDptCde", "CDptCde");
//    	map.put("TAppTmStart", "TAppTmStart");
//    	map.put("TAppTmEnd", "TAppTmEnd");
//    	map.put("TInsrncBgnTmStart", "TInsrncBgnTmStart");
//    	*/
//    	//String sql = "SELECT new map(    base.CAppNo as CAppNo,    base.CPlyNo as CPlyNo,    base.CProdNo as CProdNo,    base.CPrnNo as CPrnNo,    base.CManualMrk as CManualMrk,    base.TAppTm as TAppTm,    base.TInsrncBgnTm as TInsrncBgnTm,    base.TInsrncEndTm as TInsrncEndTm,    base.TUdrTm as TUdrTm,    (SELECT COperCnm FROM OrgOperVO WHERE base.CUdrCde IS NOT NULL AND base.CUdrCde = COperId ) as CUdrNme,     prod.CNmeCn as CProdNmeCn,    vhl.CPlateNo as CPlateNo,   applicant.CAppNme as CAppNme   )  FROM PlyBaseVO base, AppApplicantVO applicant, AppVhlVO vhl, PrdProdVO prod, OrgDptVO dpt  WHERE base.CAppNo = vhl.CAppNo  AND base.CAppNo = applicant.CAppNo  AND base.CProdNo = prod.CProdNo  AND base.CDptCde = dpt.CDptCde  AND base.CAppTyp = 'A'  AND base.CPlyNo IS NOT NULL  AND applicant.CAppNme like ':CAppNme%'  AND base.CCommonFlag in (':CCommonFlag')  AND vhl.CPlateNo = ':CPlateNo'  AND base.TAppTm >= to_date(':TAppTmStart', 'yyyy-mm-dd')  AND base.TAppTm < to_date(':TAppTmEnd', 'yyyy-mm-dd')+1  AND base.TInsrncBgnTm >= to_date(':TInsrncBgnTmStart', 'yyyy-mm-dd')  AND base.TInsrncBgnTm < to_date(':TInsrncBgnTmEnd', 'yyyy-mm-dd')+1  AND EXISTS ( SELECT CDptRelCde FROM OrgDptVO WHERE CDptCde=':CDptCde' or dpt.CDptRelCde like concat(CDptRelCde,'%') )  AND base.CProdNo in (':CProdNo')  ORDER BY base.TAppTm DESC";
//    	sql = " select       a.c_pk_id as pkId,      (select c_user_nme from t_user x where x.c_user_id=a.c_user_id) as doctorNme,       b.c_dpt_nme as dptNme,      (select c_office_nme from t_bas_office y where y.c_office_id=c.c_office_id) as officeNme,      date_format(a.t_date,'%Y-%m-%d') as date,      a.n_max_num1 as maxNum1,      a.n_num1 as num1,      a.n_max_num2 as maxNum2,      a.n_num2 as num2 from       t_work_sched a,t_bas_dpt b,t_doctor_auth c  where        (c.c_dpt_id='#CDptId' or c.c_dpt_id1='#CDptId' or c.c_dpt_id2='#CDptId')        and a.t_date<='#TEndTm' and a.t_date>='#TBgnTm'        and b.c_province='#CPro' and b.c_city='#CCity'        and a.c_dpt_id=b.c_dpt_id        and a.c_user_id=c.c_user_id        and (a.c_dpt_id=c.c_dpt_id or a.c_dpt_id=c.c_dpt_id1 or a.c_dpt_id=c.c_dpt_id2) order by        a.t_date desc";
//    	HashMap<String, Object> map = new HashMap<String, Object>();
//    	map.put("CChaMrk", "CChaMrk");
//    	map.put("CChaCde", "CChaCde");
//    	map.put("CBsnsTyp", "CBsnsTyp");
//    	map.put("CChaMrk", "CChaMrk");
//
//
//
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
				" left join "+
				"	(select * from tableA where c_lev_cde = '#CLevCde' )  a1"+
				" on a=b "+
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
				"	 and C_URL in ('#cUrl') "+
				"	 and C_SUCCESS = '#cSuccess' "+
				"	 and (field='#f1' or filed2='#f2' )"+
				"	order by T_BEGIN desc ";

		Map map = new HashMap();
		map.put("cUrl", Arrays.asList("a","b","c"));
		map.put("f1", 1);
		map.put("f2", 2);
		map.put("CLevCde", 55);

		String ql = new SqlFilterUtils().matching(sql.toString(), map);
		System.out.println(ql);
    }
}

