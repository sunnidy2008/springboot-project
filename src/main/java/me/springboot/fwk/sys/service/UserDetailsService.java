package me.springboot.fwk.sys.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.springboot.fwk.sys.UserBO;
import me.springboot.fwk.sys.dao.SysPermissionDao;
import me.springboot.fwk.sys.dao.SysRoleDao;
import me.springboot.fwk.sys.dao.SysUserDao;
import me.springboot.fwk.sys.vo.SysRoleVO;
import me.springboot.fwk.sys.vo.SysUserVO;

@Service
public class UserDetailsService {

	@Autowired
	private SysUserDao sysUserDao;
	@Autowired
	private SysRoleDao sysRoleDao;
	
	public UserBO loadUserByUsername(String username) throws Exception{
		List<SysUserVO> userList = this.sysUserDao.getSysUserJpa().findBycUsername(username);
		if(userList!=null && userList.size()>0){
			if(userList.size()>1){
				throw new Exception("根据用户名["+username+"]查询出来"+userList.size()+"条用户数据");
			}else{
				SysUserVO dbUser = userList.get(0);
				List<SysRoleVO> roleList = this.sysRoleDao.getSysRoleJpa().selectByUserId(dbUser.getcPkId());
//				List<SysPermissionVO> permissionList = this.permissonDao.getPermissonJpa().selectByUserId(dbUser.getcPkId());
				
				UserBO user = new UserBO();

				user.setId(dbUser.getcPkId());
				user.setUsername(dbUser.getcUsername());
				user.setPassword(dbUser.getcPassword());
				user.setPhone(dbUser.getcPhone());
				user.setEmail(dbUser.getcEmail());
				user.setImg(dbUser.getcImg());
				user.setNickName(dbUser.getcNickName());
				
				for(SysRoleVO tbRole:roleList){
					if(tbRole==null || tbRole.getcEnname()==null || "".equals(tbRole.getcEnname())){
						continue;
					}
//					if(tbRole.getEnname().startsWith("ROLE_")){
//						throw new Exception("角色英文名不能以ROLE_开头");
//					}else{
//						user.getRole().add("ROLE_"+tbRole.getEnname());
//					}
					user.getRole().add(tbRole.getcEnname());
				}
				
				return user;
			}
		}else{
			return null;
		}
	}
}
