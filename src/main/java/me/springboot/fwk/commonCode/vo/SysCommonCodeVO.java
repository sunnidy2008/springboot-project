package me.springboot.fwk.commonCode.vo;

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
 * 公共代码(t_sys_common_code)
 */
/**
 * 公共代码(t_sys_common_code)
 */
@Entity
@Table(name = "t_sys_common_code")
public class SysCommonCodeVO implements java.io.Serializable {

	/** 版本号 */
	private static final long serialVersionUID = 1229182722558037504L;
	
	
	/** 主键 */
	@Id
	@Column(name = "C_PK_ID"  , unique = true , nullable=false  , length = 50)
	private String cPkId;
	
	
	
	/** 代码值 */
	
	@Column(name = "C_CODE"  , nullable=false  , length = 50)
	private String cCode;
	
	
	
	/** 显示文本 */
	
	@Column(name = "C_TEXT"  , nullable=false  , length = 200)
	private String cText;
	
	
	
	/** 序号 */
	
	@Column(name = "N_SEQ" )
	private Integer nSeq;
	
	
	
	/** 父类代码 */
	
	@Column(name = "C_PARENT_CODE"  , nullable=false  , length = 50)
	private String cParentCode;
	
	
	
	/** 注释 */
	
	@Column(name = "C_COMMENT"  , length = 200)
	private String cComment;
	
	
	
	/** 是否有效（1有效，0无效） */
	
	@Column(name = "C_ENABLED"  , nullable=false  , length = 1)
	private String cEnabled;
	
	
	
	/** CSS样式 */
	
	@Column(name = "C_CSS"  , length = 50)
	private String cCss;
	
	
	
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
	
	@JSONField(name="cCode")
	public String getcCode(){return this.cCode;}
	public void setcCode(String cCode){this.cCode=cCode;}
	
	@JSONField(name="cText")
	public String getcText(){return this.cText;}
	public void setcText(String cText){this.cText=cText;}
	
	@JSONField(name="nSeq")
	public Integer getnSeq(){return this.nSeq;}
	public void setnSeq(Integer nSeq){this.nSeq=nSeq;}
	
	@JSONField(name="cParentCode")
	public String getcParentCode(){return this.cParentCode;}
	public void setcParentCode(String cParentCode){this.cParentCode=cParentCode;}
	
	@JSONField(name="cComment")
	public String getcComment(){return this.cComment;}
	public void setcComment(String cComment){this.cComment=cComment;}
	
	@JSONField(name="cEnabled")
	public String getcEnabled(){return this.cEnabled;}
	public void setcEnabled(String cEnabled){this.cEnabled=cEnabled;}
	
	@JSONField(name="cCss")
	public String getcCss(){return this.cCss;}
	public void setcCss(String cCss){this.cCss=cCss;}
	
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