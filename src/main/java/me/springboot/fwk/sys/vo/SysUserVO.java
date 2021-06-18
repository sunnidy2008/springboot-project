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
 * 用户表(t_sys_user)
 */
@Entity
@Table(name = "t_sys_user")
public class SysUserVO implements java.io.Serializable {

	/** 版本号 */
	private static final long serialVersionUID = 1642558399973357056L;
	
	
	/**  */
	@Id
	@Column(name = "C_PK_ID"  , unique = true , nullable=false  , length = 50)
	private String cPkId;
	
	
	
	/** 用户名 */
	
	@Column(name = "C_USERNAME"  , unique = true , nullable=false  , length = 50)
	private String cUsername;
	
	
	
	/** 密码 */
	
	@Column(name = "C_PASSWORD"  , length = 200)
	private String cPassword;
	
	
	
	/** 性别 */
	
	@Column(name = "C_SEX"  , length = 1)
	private String cSex;
	
	
	
	/** 昵称 */
	
	@Column(name = "C_NICK_NAME"  , length = 50)
	private String cNickName;
	
	
	
	/** 头像地址 */
	
	@Column(name = "C_IMG"  , length = 200)
	private String cImg;
	
	
	
	/** 注册手机号 */
	
	@Column(name = "C_PHONE"  , unique = true , length = 50)
	private String cPhone;
	
	
	
	/** 注册邮箱 */
	
	@Column(name = "C_EMAIL"  , unique = true , length = 50)
	private String cEmail;
	
	
	
	/** 是否生效（0失效，1生效） */
	
	@Column(name = "C_ENABLED"  , nullable=false  , length = 1)
	private String cEnabled;
	
	
	
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
	
	@JSONField(name="cUsername")
	public String getcUsername(){return this.cUsername;}
	public void setcUsername(String cUsername){this.cUsername=cUsername;}
	
	@JSONField(name="cPassword")
	public String getcPassword(){return this.cPassword;}
	public void setcPassword(String cPassword){this.cPassword=cPassword;}
	
	@JSONField(name="cSex")
	public String getcSex(){return this.cSex;}
	public void setcSex(String cSex){this.cSex=cSex;}
	
	@JSONField(name="cNickName")
	public String getcNickName(){return this.cNickName;}
	public void setcNickName(String cNickName){this.cNickName=cNickName;}
	
	@JSONField(name="cImg")
	public String getcImg(){return this.cImg;}
	public void setcImg(String cImg){this.cImg=cImg;}
	
	@JSONField(name="cPhone")
	public String getcPhone(){return this.cPhone;}
	public void setcPhone(String cPhone){this.cPhone=cPhone;}
	
	@JSONField(name="cEmail")
	public String getcEmail(){return this.cEmail;}
	public void setcEmail(String cEmail){this.cEmail=cEmail;}
	
	@JSONField(name="cEnabled")
	public String getcEnabled(){return this.cEnabled;}
	public void setcEnabled(String cEnabled){this.cEnabled=cEnabled;}
	
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