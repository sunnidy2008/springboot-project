package me.springboot.fwk.sys.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import lombok.Getter;
import lombok.Setter;
import me.springboot.fwk.db.DbUtils;
import me.springboot.fwk.db.page.PageResult;
import me.springboot.fwk.result.R;
import me.springboot.fwk.result.Result;
import me.springboot.fwk.security.MyBCryptPasswordEncoder;
import me.springboot.fwk.sys.UserUtils;
import me.springboot.fwk.sys.dao.SysUserDao;
import me.springboot.fwk.sys.dao.SysUserRoleDao;
import me.springboot.fwk.sys.service.SysUserRoleService;
import me.springboot.fwk.sys.service.SysUserService;
import me.springboot.fwk.sys.vo.SysUserRoleVO;
import me.springboot.fwk.sys.vo.SysUserVO;
import me.springboot.fwk.utils.BeanUtils;

@RestController
@Getter
@Setter
public class SysUserController {

	@Autowired
	private SysUserDao sysUserDao;
	@Resource(name="passwordEncoder")
	private MyBCryptPasswordEncoder passwordEncoder;
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysUserRoleDao sysUserRoleDao;
	@Autowired
	private SysUserRoleService sysUserRoleService;
	@Autowired
	private DbUtils dbUtils;
	
	private final static String defualtPassword="$2a$12$r4vIbuQhkNuVu/OjpPex..LhZTE5VZOaJ/red85Fc2DfKdNhx2XT.";//默认密码123456 未加盐 BCryptPassword加密
	/**
	 * 获取当前登录用户
	 * @return
	 */
	@PostMapping(name="获取当前登录用户",value="/api/user/me")
	public Result me(){
		return R.success(UserUtils.getUser());
	}
	
	/**
	 * 修改当前用户的密码
	 * @return
	 */
	@PostMapping(name="修改当前用户的密码",value="/api/user/password")
	public Result password(@RequestBody JSONObject params){
		String oldPassword = params.getString("oldPassword");
		String newPassword = params.getString("newPassword");
		
		SysUserVO user = this.sysUserDao.getSysUserJpa().findById(UserUtils.getUser().getId()).get();
		if(!passwordEncoder.isMatch(oldPassword,user.getcPassword())){
			return R.error("原始密码不正确");
		}
		user.setcPassword(passwordEncoder.encoder(newPassword));
		this.sysUserDao.getSysUserJpa().save(user);
		return R.success("密码修改成功");
	}
	
	//按主键查找数据对象
	@PostMapping(name="按主键查找用户详细",value="/api/sysUser/detail")
	public Result detail(@RequestBody JSONObject params){
		String id = params.getString("id");
		SysUserVO bean = this.getSysUserService().findByPrimaryKey(id);
		List<String> roleList = this.sysUserRoleDao.getSysUserRoleJpa().getUserRoleList(bean.getcPkId());
		
		JSONObject result = (JSONObject) JSONObject.toJSON(bean);
		result.put("cRoles", roleList);
		return R.success(result);
	}
	
	//按主键删除对象
	@PostMapping(name="按主键删除制定用户",value="/api/sysUser/delete")
	@Transactional
	public Result delete(@RequestBody JSONObject params){
		String id = params.getString("id");
		this.getSysUserService().deleteByPrimaryKey(id);
		return R.success();
	}
	
	//保存或更新对象
	@PostMapping(name="保存或更新用户信息",value="/api/sysUser")
	@Transactional
	public Result saveOrUpdate(@RequestBody JSONObject params) throws Exception{
		JSONArray roles = params.getJSONArray("cRoles");
		SysUserVO bean = new SysUserVO();
		BeanUtils.map2Bean(params,bean);
		
		List<SysUserVO> dbUserList = this.sysUserDao.getSysUserJpa().findBycUsername(bean.getcUsername());
		if(dbUserList!=null && dbUserList.size()>0){
			SysUserVO tmp = dbUserList.get(0);
			if(!tmp.getcPkId().equals(bean.getcPkId())){
				return R.error("用户名已存在");
			}
		}
		
		if(StringUtils.isBlank(bean.getcPassword())){
			bean.setcPkId(UUID.randomUUID().toString());
			bean.setcPassword(defualtPassword);
			//添加后端未选的角色
			for(Object item : roles){
				SysUserRoleVO userRoleBean = new SysUserRoleVO();
				userRoleBean.setcRoleId(item.toString());
				userRoleBean.setcUserId(bean.getcPkId());
				this.sysUserRoleService.saveOrUpdate(userRoleBean);
			}
		}else{
			List<SysUserRoleVO> list = this.sysUserRoleDao.getSysUserRoleJpa().findBycUserId(bean.getcPkId());
			List<String> dbRoleList = new ArrayList<String>();
			//删除前端未选的角色
			for(SysUserRoleVO userRole : list){
				dbRoleList.add(userRole.getcRoleId());
				if(!roles.contains(userRole.getcRoleId())){
					this.sysUserRoleDao.getSysUserRoleJpa().deleteById(userRole.getcPkId());
				}
			}
			//添加后端未选的角色
			for(Object item : roles){
				if(!dbRoleList.contains(item.toString())){
					SysUserRoleVO userRoleBean = new SysUserRoleVO();
					userRoleBean.setcRoleId(item.toString());
					userRoleBean.setcUserId(bean.getcPkId());
					this.sysUserRoleService.saveOrUpdate(userRoleBean);
				}
			}
		}
		this.getSysUserService().saveOrUpdate(bean);
		return R.success(JSONObject.toJSON(bean));
	}
	
	//超管重置密码
	@PostMapping(name="超管强制设置指定用户的密码为123456",value="/api/sysUser/rest")
	@Transactional
	public Result rest(@RequestBody JSONObject params) throws Exception{
		SysUserVO bean = this.sysUserDao.getSysUserJpa().findById(params.getString("id")).get();
		bean.setcPassword(defualtPassword);//123456 无盐
		this.getSysUserService().saveOrUpdate(bean);
		return R.success(JSONObject.toJSON(bean));
	}
	
	//列表查询
	@PostMapping(name="用户列表查询",value="/api/sysUser/list")
	public Result get(@RequestBody JSONObject params) throws Exception{
		String sql = " select "+
					 "	 C_PK_ID as cPkId, "+
					 "	 C_USERNAME as cUsername, "+
					 "	 C_PASSWORD as cPassword, "+
					 "	 C_SEX as cSex, "+
					 "	 C_NICK_NAME as cNickName, "+
					 "	 C_IMG as cImg, "+
					 "	 C_PHONE as cPhone, "+
					 "	 C_EMAIL as cEmail, "+
					 "	 C_ENABLED as cEnabled, "+
					 "	 date_format(T_CRT_TM,'%Y-%m-%d %H:%i:%S') as tCrtTm, "+
					 "	 date_format(T_UPD_TM,'%Y-%m-%d %H:%i:%S') as tUpdTm, "+
					 "	 C_CRT_ID as cCrtId, "+
					 "	 C_UPD_ID as cUpdId "+
					 " from "+
					 " 	 t_sys_user"+
					 " where "+
					 "	 C_PK_ID = '#cPkId' "+
					 "	 and C_USERNAME = '#cUsername' "+
					 "	 and C_PASSWORD = '#cPassword' "+
					 "	 and C_SEX = '#cSex' "+
					 "	 and C_NICK_NAME = '#cNickName' "+
					 "	 and C_IMG = '#cImg' "+
					 "	 and C_PHONE = '#cPhone' "+
					 "	 and C_EMAIL = '#cEmail' "+
					 "	 and C_ENABLED = '#cEnabled' "+
					 "	 and T_CRT_TM = '#tCrtTm' "+
					 "	 and T_UPD_TM = '#tUpdTm' "+
					 "	 and C_CRT_ID = '#cCrtId' "+
					 "	 and C_UPD_ID = '#cUpdId' ";
		PageResult pageResult = dbUtils.queryPageSql(sql,params);
		return R.success(pageResult);
	}
}
