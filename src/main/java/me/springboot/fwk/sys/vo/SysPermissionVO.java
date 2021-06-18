package me.springboot.fwk.sys.vo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.Date;
import java.lang.String;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
/**
 * 权限表(t_sys_permission)
 */
@Entity
@Table(name = "t_sys_permission")
public class SysPermissionVO implements java.io.Serializable {

	/** 版本号 */
	private static final long serialVersionUID = 1338554003370845440L;
	
	
	/**  */
	@Id
	@Column(name = "C_PK_ID"  , unique = true , nullable=false  , length = 50)
	private String cPkId;
	
	
	
	/** 权限名称 */
	
	@Column(name = "C_NAME"  , nullable=false  , length = 50)
	private String cName;
	
	
	
	/** 权限英文名称 */
	
	@Column(name = "C_ENNAME"  , nullable=false  , length = 50)
	private String cEnname;
	
	
	
	/** 授权路径 */
	
	@Column(name = "C_URL"  , nullable=false  , length = 200)
	private String cUrl;
	
	
	
	/** 方法名（get，post，delete等，多个使用英文逗号分隔） */
	
	@Column(name = "C_METHOD"  , nullable=false  , length = 50)
	private String cMethod;
	
	
	
	/** 备注 */
	
	@Column(name = "C_DESCRIPTION"  , nullable=false  , length = 200)
	private String cDescription;
	
	
	
	/**  */
	
	@Column(name = "T_CRT_TM"  , nullable=false )
	private Date tCrtTm;
	
	
	
	/**  */
	
	@Column(name = "T_UPD_TM"  , nullable=false )
	private Date tUpdTm;
	
	
	
	/** 创建人id */
	
	@Column(name = "C_CRT_ID"  , nullable=false  , length = 50)
	private String cCrtId;
	
	
	
	/** 修改人id */
	
	@Column(name = "C_UPD_ID"  , nullable=false  , length = 50)
	private String cUpdId;
	
	
	
	
	@JSONField(name="cPkId")
	public String getcPkId(){return this.cPkId;}
	public void setcPkId(String cPkId){this.cPkId=cPkId;}
	
	@JSONField(name="cName")
	public String getcName(){return this.cName;}
	public void setcName(String cName){this.cName=cName;}
	
	@JSONField(name="cEnname")
	public String getcEnname(){return this.cEnname;}
	public void setcEnname(String cEnname){this.cEnname=cEnname;}
	
	@JSONField(name="cUrl")
	public String getcUrl(){return this.cUrl;}
	public void setcUrl(String cUrl){this.cUrl=cUrl;}
	
	@JSONField(name="cMethod")
	public String getcMethod(){return this.cMethod;}
	public void setcMethod(String cMethod){this.cMethod=cMethod;}
	
	@JSONField(name="cDescription")
	public String getcDescription(){return this.cDescription;}
	public void setcDescription(String cDescription){this.cDescription=cDescription;}
	
	@JSONField(name="tCrtTm")
	public Date gettCrtTm(){return this.tCrtTm;}
	public void settCrtTm(Date tCrtTm){this.tCrtTm=tCrtTm;}
	
	@JSONField(name="tUpdTm")
	public Date gettUpdTm(){return this.tUpdTm;}
	public void settUpdTm(Date tUpdTm){this.tUpdTm=tUpdTm;}
	
	@JSONField(name="cCrtId")
	public String getcCrtId(){return this.cCrtId;}
	public void setcCrtId(String cCrtId){this.cCrtId=cCrtId;}
	
	@JSONField(name="cUpdId")
	public String getcUpdId(){return this.cUpdId;}
	public void setcUpdId(String cUpdId){this.cUpdId=cUpdId;}
	
	
}