package me.springboot.fwk.sys.vo;

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
 * 菜单表(t_sys_menu)
 */
@Entity
@Table(name = "t_sys_menu")
public class SysMenuVO implements java.io.Serializable {

	/** 版本号 */
	private static final long serialVersionUID = 1443649124197742080L;
	
	
	/** 菜单编码 */
	@Id
	@Column(name = "C_MENU_CODE"  , unique = true , nullable=false  , length = 10)
	private String cMenuCode;
	
	
	
	/** 菜单名称 */
	
	@Column(name = "C_MENU_NAME"  , length = 50)
	private String cMenuName;
	
	
	
	/** URL */
	
	@Column(name = "C_URL"  , length = 50)
	private String cUrl;
	
	
	
	/** 顺序 */
	
	@Column(name = "N_NUM" )
	private Integer nNum;
	
	
	
	/** 层级 */
	
	@Column(name = "N_LEVEL" )
	private Integer nLevel;
	
	
	
	/** 父级编码 */
	
	@Column(name = "C_PARENT_CODE"  , length = 10)
	private String cParentCode;
	
	
	
	/** 是否有效(0-失效，1-生效，2-虚拟节点） */
	
	@Column(name = "C_STATUS"  , length = 10)
	private String cStatus;
	
	
	
	/** 失效时间 */
	
	@Column(name = "D_INVALID_TIME" )
	private Date dInvalidTime;
	
	
	
	
	@JSONField(name="cMenuCode")
	public String getcMenuCode(){return this.cMenuCode;}
	public void setcMenuCode(String cMenuCode){this.cMenuCode=cMenuCode;}
	
	@JSONField(name="cMenuName")
	public String getcMenuName(){return this.cMenuName;}
	public void setcMenuName(String cMenuName){this.cMenuName=cMenuName;}
	
	@JSONField(name="cUrl")
	public String getcUrl(){return this.cUrl;}
	public void setcUrl(String cUrl){this.cUrl=cUrl;}
	
	@JSONField(name="nNum")
	public Integer getnNum(){return this.nNum;}
	public void setnNum(Integer nNum){this.nNum=nNum;}
	
	@JSONField(name="nLevel")
	public Integer getnLevel(){return this.nLevel;}
	public void setnLevel(Integer nLevel){this.nLevel=nLevel;}
	
	@JSONField(name="cParentCode")
	public String getcParentCode(){return this.cParentCode;}
	public void setcParentCode(String cParentCode){this.cParentCode=cParentCode;}
	
	@JSONField(name="cStatus")
	public String getcStatus(){return this.cStatus;}
	public void setcStatus(String cStatus){this.cStatus=cStatus;}
	
	@JSONField(name="dInvalidTime")
	public Date getdInvalidTime(){return this.dInvalidTime;}
	public void setdInvalidTime(Date dInvalidTime){this.dInvalidTime=dInvalidTime;}
	
	
}