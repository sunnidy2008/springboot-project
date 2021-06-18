package me.springboot.fwk.websocket.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import lombok.Getter;
import lombok.Setter;
import me.springboot.fwk.db.DbUtils;
import me.springboot.fwk.db.page.PageResult;
import me.springboot.fwk.result.R;
import me.springboot.fwk.result.Result;
import me.springboot.fwk.sys.UserUtils;
import me.springboot.fwk.utils.BeanUtils;
import me.springboot.fwk.utils.VoUtils;
import me.springboot.fwk.websocket.service.SysWsMessageService;
import me.springboot.fwk.websocket.vo.SysWsMessageVO;

@RestController
@Getter
@Setter
public class SysWsMessageController<SysWsWebsocketVO>{

	@Autowired
	private SysWsMessageService sysWsMessageService;
	@Autowired
	private DbUtils dbUtils;
	
	//按主键查找数据对象
	@PostMapping(value="/api/sysWsMessage/detail",name="按主键获取websocket消息的详细记录")
	public Result detail(@RequestBody JSONObject params){
		String id = params.getString("id");
		SysWsMessageVO bean = this.getSysWsMessageService().findByPrimaryKey(id);
		return R.success(JSONObject.toJSON(bean));
	}
	
	//按主键删除对象
	@PostMapping(value="/api/sysWsMessage/delete",name="按主键删除websocket消息的指定记录")
	@Transactional
	public Result delete(@RequestBody JSONObject params){
		String id = params.getString("id");
		this.getSysWsMessageService().deleteByPrimaryKey(id);
		return R.success();
	}
	
	//保存或更新对象
	@PostMapping(value="/api/sysWsMessage",name="保存或更新数据到websocket消息")
	@Transactional
	public Result saveOrUpdate(@RequestBody JSONObject params) throws Exception{
		SysWsMessageVO bean = new SysWsMessageVO();
		BeanUtils.map2Bean(params,bean);
		this.getSysWsMessageService().saveOrUpdate(bean);
		return R.success(JSONObject.toJSON(bean));
	}
	
	//列表查询
	@PostMapping(value="/api/sysWsMessage/list",name="列表查询websocket消息的数据")
	public Result list(@RequestBody JSONObject params) throws Exception{
		String sql = " select "+
					 "	 C_PK_ID as cPkId, "+
					 "	 C_FROM_USER_ID as cFromUserId, "+
					 "	 C_TO_USER_ID as cToUserId, "+
					 "	 C_TYPE as cType, "+
					 "	 C_READ_MRK as cReadMrk, "+
					 "	 C_TEXT as cText, "+
					 "	 C_URL as cUrl, "+
					 "	 C_EXTRA_DATA as cExtraData, "+
					 "	 date_format(T_SEND_TM,'%Y-%m-%d %H:%i:%S') as tSendTm, "+
					 "	 date_format(T_READ_TM,'%Y-%m-%d %H:%i:%S') as tReadTm "+
					 " from "+
					 " 	 t_sys_ws_message"+
					 " where "+
					 "	 C_PK_ID = '#cPkId' "+
					 "	 and C_FROM_USER_ID = '#cFromUserId' "+
					 "	 and C_TO_USER_ID = '#cToUserId' "+
					 "	 and C_TYPE = '#cType' "+
					 "	 and C_READ_MRK = '#cReadMrk' "+
					 "	 and C_TEXT = '#cText' "+
					 "	 and C_URL = '#cUrl' "+
					 "	 and C_EXTRA_DATA = '#cExtraData' "+
					 "	 and T_SEND_TM = '#tSendTm' "+
					 "	 and T_READ_TM = '#tReadTm' "+
					 "	order by C_READ_MRK ,T_SEND_TM desc";
		PageResult pageResult = dbUtils.queryPageSql(sql,params);
		return R.success(pageResult);
	}
	
	//保存或更新对象
	@PostMapping(value="/api/sysWsMessage/read",name="设置消息为已读")
	@Transactional
	public Result read(@RequestBody JSONObject params) throws Exception{
		String id = params.getString("id");
		SysWsMessageVO bean = (SysWsMessageVO)this.sysWsMessageService.getSysWsMessageDao().getSysWsMessageJpa().findById(id).get();
		bean.setcReadMrk("Y");
		Date now = new Date();
		bean.settReadTm(now);
		VoUtils.touchOnUpdate(bean, now, UserUtils.getUser().getId());
		this.sysWsMessageService.getSysWsMessageDao().getSysWsMessageJpa().save(bean);
		return R.success(JSONObject.toJSON(bean));
	}
	
	
	@PostMapping(name="获取当前用户的未读消息条数",value="/api/sysWsMessage/unread")
	public Result unread(){
		String sql = "select count(1) as num from t_sys_ws_message where c_to_user_id=? and c_read_mrk='N'";
		List list = dbUtils.querySql(sql, UserUtils.getUser().getId());
		int unread = 0;
		if(list !=null && list.size()>0){
			unread = Integer.parseInt(((Map)list.get(0)).get("num").toString());
		}
		return R.success(unread);
	}
}
