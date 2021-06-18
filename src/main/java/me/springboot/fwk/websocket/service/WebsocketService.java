package me.springboot.fwk.websocket.service;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;

import lombok.Getter;
import lombok.Setter;
import me.springboot.fwk.sys.UserUtils;
import me.springboot.fwk.utils.IDUtils;
import me.springboot.fwk.utils.VoUtils;
import me.springboot.fwk.websocket.dao.SysWsMessageDao;
import me.springboot.fwk.websocket.server.WebSocketServer;
import me.springboot.fwk.websocket.vo.SysWsMessageVO;

@Service
@Getter
@Setter
public class WebsocketService {

	@Autowired
	private SysWsMessageDao sysWsMessageDao;
	
	
	//新建事务保存
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void sendMsg(SysWsMessageVO bean){
		try {
			bean.setcReadMrk("N");
			if(bean.getcFromUserId()==null){
				if(UserUtils.getUser()!=null){
					bean.setcFromUserId(UserUtils.getUser().getId());
				}else{
					bean.setcFromUserId("SYS");
				}
			}
			Date now = new Date();
			VoUtils.touchOnCreate(bean, now, bean.getcFromUserId());
			if(bean.gettSendTm()==null){
				bean.settSendTm(now);
			}
			bean.setcPkId(IDUtils.getUUID32());
			WebSocketServer.sendInfo(JSON.toJSONString(bean),bean.getcToUserId());
			this.sysWsMessageDao.getSysWsMessageJpa().save(bean);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
