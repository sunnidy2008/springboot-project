package me.springboot.fwk.sys.vo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.lang.String;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
/**
 * 省市县区街道基础数据(t_sys_address)
 */
@Entity
@Table(name = "t_sys_address")
public class SysAddressVO implements java.io.Serializable {

	/** 版本号 */
	private static final long serialVersionUID = 1498966123308445952L;
	
	
	/** 代码 */
	@Id
	@Column(name = "C_CODE"  , unique = true , nullable=false  , length = 50)
	private String cCode;
	
	
	
	/** 名称 */
	
	@Column(name = "C_NAME"  , nullable=false  , length = 50)
	private String cName;
	
	
	
	/** 上级代码 */
	
	@Column(name = "C_PARENT"  , nullable=false  , length = 50)
	private String cParent;
	
	
	
	/** privince省,city市,area县区,street街道 */
	
	@Column(name = "C_TYPE"  , nullable=false  , length = 50)
	private String cType;
	
	
	
	
	@JSONField(name="cCode")
	public String getcCode(){return this.cCode;}
	public void setcCode(String cCode){this.cCode=cCode;}
	
	@JSONField(name="cName")
	public String getcName(){return this.cName;}
	public void setcName(String cName){this.cName=cName;}
	
	@JSONField(name="cParent")
	public String getcParent(){return this.cParent;}
	public void setcParent(String cParent){this.cParent=cParent;}
	
	@JSONField(name="cType")
	public String getcType(){return this.cType;}
	public void setcType(String cType){this.cType=cType;}
	
	
}