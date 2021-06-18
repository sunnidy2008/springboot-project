package me.springboot.fwk.websocket.vo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.Date;
import java.lang.String;

import com.alibaba.fastjson.annotation.JSONField;
/**
 * websocket消息(t_sys_ws_message)
 */
@Entity
@Table(name = "t_sys_ws_message")
public class SysWsMessageVO implements java.io.Serializable {

	/** 版本号 */
	private static final long serialVersionUID = 1618695096261087488L;
	
	
	/** 主键 */
	@Id
	@Column(name = "C_PK_ID"  , unique = true , nullable=false  , length = 50)
	private String cPkId;
	
	
	
	/** 发送者用户id */
	
	@Column(name = "C_FROM_USER_ID"  , nullable=false  , length = 50)
	private String cFromUserId;
	
	
	
	/** 接受者用户id */
	
	@Column(name = "C_TO_USER_ID"  , nullable=false  , length = 50)
	private String cToUserId;
	
	
	
	/** 消息类型（预留） */
	
	@Column(name = "C_TYPE"  , length = 50)
	private String cType;
	
	
	
	/** 消息是否已读，Y已读,N未读 */
	
	@Column(name = "C_READ_MRK"  , length = 1)
	private String cReadMrk;
	
	
	
	/** 消息文本 */
	
	@Column(name = "C_TEXT"  , length = 200)
	private String cText;
	
	
	
	/** 点击消息响应的url */
	
	@Column(name = "C_URL"  , length = 200)
	private String cUrl;
	
	
	
	/** 消息附带的其他参数 */
	
	@Column(name = "C_EXTRA_DATA"  , length = 500)
	private String cExtraData;
	
	
	
	/** 消息发送时间 */
	
	@Column(name = "T_SEND_TM"  , nullable=false )
	private Date tSendTm;
	
	
	
	/** 消息阅读时间 */
	
	@Column(name = "T_READ_TM" )
	private Date tReadTm;
	
	
	
	/**  */
	
	@Column(name = "T_CRT_TM" )
	private Date tCrtTm;
	
	
	
	/**  */
	
	@Column(name = "T_UPD_TM" )
	private Date tUpdTm;
	
	
	
	/**  */
	
	@Column(name = "C_CRT_ID"  , length = 50)
	private String cCrtId;
	
	
	
	/**  */
	
	@Column(name = "C_UPD_ID"  , length = 50)
	private String cUpdId;
	
	
	
	
	@JSONField(name="cPkId")
	public String getcPkId(){return this.cPkId;}
	public void setcPkId(String cPkId){this.cPkId=cPkId;}
	
	@JSONField(name="cFromUserId")
	public String getcFromUserId(){return this.cFromUserId;}
	public void setcFromUserId(String cFromUserId){this.cFromUserId=cFromUserId;}
	
	@JSONField(name="cToUserId")
	public String getcToUserId(){return this.cToUserId;}
	public void setcToUserId(String cToUserId){this.cToUserId=cToUserId;}
	
	@JSONField(name="cType")
	public String getcType(){return this.cType;}
	public void setcType(String cType){this.cType=cType;}
	
	@JSONField(name="cReadMrk")
	public String getcReadMrk(){return this.cReadMrk;}
	public void setcReadMrk(String cReadMrk){this.cReadMrk=cReadMrk;}
	
	@JSONField(name="cText")
	public String getcText(){return this.cText;}
	public void setcText(String cText){this.cText=cText;}
	
	@JSONField(name="cUrl")
	public String getcUrl(){return this.cUrl;}
	public void setcUrl(String cUrl){this.cUrl=cUrl;}
	
	@JSONField(name="cExtraData")
	public String getcExtraData(){return this.cExtraData;}
	public void setcExtraData(String cExtraData){this.cExtraData=cExtraData;}
	
	@JSONField(name="tSendTm")
	public Date gettSendTm(){return this.tSendTm;}
	public void settSendTm(Date tSendTm){this.tSendTm=tSendTm;}
	
	@JSONField(name="tReadTm")
	public Date gettReadTm(){return this.tReadTm;}
	public void settReadTm(Date tReadTm){this.tReadTm=tReadTm;}
	
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