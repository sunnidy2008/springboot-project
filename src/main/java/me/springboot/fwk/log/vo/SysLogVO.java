package me.springboot.fwk.log.vo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.Date;
import java.lang.String;
import java.lang.Integer;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
/**
 * 系统日志表(t_sys_log)
 */
@Entity
@Table(name = "t_sys_log")
public class SysLogVO implements java.io.Serializable {

	/** 版本号 */
	private static final long serialVersionUID = 1848783171431751168L;
	
	
	/** 主键 */
	@Id
	@Column(name = "C_PK_ID"  , unique = true , nullable=false  , length = 50)
	private String cPkId;
	
	
	
	/** 方法名 */
	
	@Column(name = "C_NAME"  , length = 200)
	private String cName;
	
	
	
	/** 操作人 */
	
	@Column(name = "C_USER_ID"  , length = 50)
	private String cUserId;
	
	
	
	/** 客户端ip */
	
	@Column(name = "C_CLIENT_IP"  , length = 50)
	private String cClientIp;
	
	
	
	/** 服务器ip */
	
	@Column(name = "C_SERVER_IP"  , length = 50)
	private String cServerIp;
	
	
	
	/** 服务器端口 */
	
	@Column(name = "C_SERVER_PORT"  , length = 50)
	private String cServerPort;
	
	
	
	/** 请求开始时间 */
	
	@Column(name = "T_BEGIN" )
	private Date tBegin;
	
	
	
	/** 请求结束时间 */
	
	@Column(name = "T_END" )
	private Date tEnd;
	
	
	
	/** 请求耗时 */
	
	@Column(name = "N_TOOK" )
	private Integer nTook;
	
	
	
	/** 请求方法 */
	
	@Column(name = "C_METHOD"  , length = 200)
	private String cMethod;
	
	
	
	/** 请求类 */
	
	@Column(name = "C_CLASS"  , length = 200)
	private String cClass;
	
	
	
	/** 请求数据 */
	
	@Column(name = "C_REQUEST" )
	private String cRequest;
	
	
	
	/** 返回数据 */
	
	@Column(name = "C_RESPONSE" )
	private String cResponse;
	
	
	
	/** 请求url */
	
	@Column(name = "C_URL"  , length = 200)
	private String cUrl;
	
	
	
	/** 结果成功与否 */
	
	@Column(name = "C_SUCCESS"  , length = 10)
	private String cSuccess;
	
	
	
	
	@JSONField(name="cPkId")
	public String getcPkId(){return this.cPkId;}
	public void setcPkId(String cPkId){this.cPkId=cPkId;}
	
	@JSONField(name="cName")
	public String getcName(){return this.cName;}
	public void setcName(String cName){this.cName=cName;}
	
	@JSONField(name="cUserId")
	public String getcUserId(){return this.cUserId;}
	public void setcUserId(String cUserId){this.cUserId=cUserId;}
	
	@JSONField(name="cClientIp")
	public String getcClientIp(){return this.cClientIp;}
	public void setcClientIp(String cClientIp){this.cClientIp=cClientIp;}
	
	@JSONField(name="cServerIp")
	public String getcServerIp(){return this.cServerIp;}
	public void setcServerIp(String cServerIp){this.cServerIp=cServerIp;}
	
	@JSONField(name="cServerPort")
	public String getcServerPort(){return this.cServerPort;}
	public void setcServerPort(String cServerPort){this.cServerPort=cServerPort;}
	
	@JSONField(name="tBegin")
	public Date gettBegin(){return this.tBegin;}
	public void settBegin(Date tBegin){this.tBegin=tBegin;}
	
	@JSONField(name="tEnd")
	public Date gettEnd(){return this.tEnd;}
	public void settEnd(Date tEnd){this.tEnd=tEnd;}
	
	@JSONField(name="nTook")
	public Integer getnTook(){return this.nTook;}
	public void setnTook(Integer nTook){this.nTook=nTook;}
	
	@JSONField(name="cMethod")
	public String getcMethod(){return this.cMethod;}
	public void setcMethod(String cMethod){this.cMethod=cMethod;}
	
	@JSONField(name="cClass")
	public String getcClass(){return this.cClass;}
	public void setcClass(String cClass){this.cClass=cClass;}
	
	@JSONField(name="cRequest")
	public String getcRequest(){return this.cRequest;}
	public void setcRequest(String cRequest){this.cRequest=cRequest;}
	
	@JSONField(name="cResponse")
	public String getcResponse(){return this.cResponse;}
	public void setcResponse(String cResponse){this.cResponse=cResponse;}
	
	@JSONField(name="cUrl")
	public String getcUrl(){return this.cUrl;}
	public void setcUrl(String cUrl){this.cUrl=cUrl;}
	
	@JSONField(name="cSuccess")
	public String getcSuccess(){return this.cSuccess;}
	public void setcSuccess(String cSuccess){this.cSuccess=cSuccess;}
	
	
}