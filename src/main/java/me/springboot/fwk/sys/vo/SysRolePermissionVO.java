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
 * 角色权限表(t_sys_role_permission)
 */
@Entity
@Table(name = "t_sys_role_permission")
public class SysRolePermissionVO implements java.io.Serializable {

	/** 版本号 */
	private static final long serialVersionUID = 1546909768565720832L;
	
	
	/**  */
	@Id
	@Column(name = "C_PK_ID"  , unique = true , nullable=false  , length = 50)
	private String cPkId;
	
	
	
	/** 角色 ID */
	
	@Column(name = "C_ROLE_ID"  , nullable=false  , length = 50)
	private String cRoleId;
	
	
	
	/** 权限 ID */
	
	@Column(name = "C_PERMISSION_ID"  , nullable=false  , length = 50)
	private String cPermissionId;
	
	
	
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
	
	@JSONField(name="cRoleId")
	public String getcRoleId(){return this.cRoleId;}
	public void setcRoleId(String cRoleId){this.cRoleId=cRoleId;}
	
	@JSONField(name="cPermissionId")
	public String getcPermissionId(){return this.cPermissionId;}
	public void setcPermissionId(String cPermissionId){this.cPermissionId=cPermissionId;}
	
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