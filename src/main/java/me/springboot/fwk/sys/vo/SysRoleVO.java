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
 * 角色表(t_sys_role)
 */
@Entity
@Table(name = "t_sys_role")
public class SysRoleVO implements java.io.Serializable {

	/** 版本号 */
	private static final long serialVersionUID = 1436284022661920768L;
	
	
	/**  */
	@Id
	@Column(name = "C_PK_ID"  , unique = true , nullable=false  , length = 50)
	private String cPkId;
	
	
	
	/** 角色名称 */
	
	@Column(name = "C_NAME"  , nullable=false  , length = 50)
	private String cName;
	
	
	
	/** 角色英文名称 */
	
	@Column(name = "C_ENNAME"  , unique = true , nullable=false  , length = 50)
	private String cEnname;
	
	
	
	/** 备注 */
	
	@Column(name = "C_DESCRIPTION"  , length = 200)
	private String cDescription;
	
	
	
	/**  */
	
	@Column(name = "T_CRT_TM"  , nullable=false )
	private Date tCrtTm;
	
	
	
	/**  */
	
	@Column(name = "T_UPD_TM"  , nullable=false )
	private Date tUpdTm;
	
	
	
	/**  */
	
	@Column(name = "C_CRT_ID"  , nullable=false  , length = 50)
	private String cCrtId;
	
	
	
	/**  */
	
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