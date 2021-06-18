package me.springboot.fwk.log;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import me.springboot.fwk.db.DbUtils;
import me.springboot.fwk.utils.IDUtils;

@Service
public class LogService {

	@Autowired
	private DbUtils dbUtils;
	
	//日志单独事务提交
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void save(String name,
						String userCode,
						String clientIp,
						String serverIp,
						String serverPort,
						Date bgn,
						Date end,
						Long took,
						String method,
						String clazz,
						String request,
						String response,
						String url,
						String success){
		String sql = "insert into t_sys_log (  `C_PK_ID`,"+
											  "`C_NAME`,"+
											  "`C_USER_ID`,"+
											  "`C_CLIENT_IP`,"+
											  "`C_SERVER_IP`,"+
											  "`C_SERVER_PORT`,"+
											  "`T_BEGIN`,"+
											  "`T_END`,"+
											  "`N_TOOK`,"+
											  "`C_METHOD`,"+
											  "`C_CLASS`,"+
											  "`C_REQUEST`,"+
											  "`C_RESPONSE`,"+
											  "`C_URL`,"+
											  "`C_SUCCESS`) "+
											" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		dbUtils.updateSql(sql,
							IDUtils.getUUID32(),
							name,
							userCode,
							clientIp,
							serverIp,
							serverPort,
							bgn,
							end,
							took,
							method,
							clazz,
							request,
							response,
							url,
							success);
	}
}
