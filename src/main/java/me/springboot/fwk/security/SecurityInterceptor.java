package me.springboot.fwk.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.springboot.fwk.exception.CustomException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerInterceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;
import me.springboot.fwk.properties.SysProperties;
import me.springboot.fwk.redis.RedisUtils;
import me.springboot.fwk.sys.UserBO;
import me.springboot.fwk.sys.UserUtils;
import me.springboot.fwk.sys.dao.SysPermissionDao;
import me.springboot.fwk.utils.AjaxUtils;
import me.springboot.fwk.utils.CookieUtils;

@Component
@Slf4j
public class SecurityInterceptor implements HandlerInterceptor {

	@Autowired
	private SysProperties sysProperties;
	private Long timeout=30*60L;//延长30分钟
	private String ssoServerAuthUrl= "/ssoauth";//sso服务器认证地址
	private String cookieHeadKey="TOKEN_";
	
	@Autowired
	private RedisUtils redisUtils;
	@Autowired
	private SysPermissionDao sysPermissionDao;
	
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
	    	

		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		// TODO Auto-generated method stub
		String uri = request.getRequestURI();//请求的uri地址
		String queryString = request.getQueryString();
		String requestUrl = request.getRequestURL().toString();
		String contentPath = request.getContextPath();//工程名
		String method = request.getMethod();
		
		String matcherPath = uri.replace(contentPath, "");//踢出掉工程名的相对路径，不含请求参数
		if("/".equals(matcherPath))matcherPath="";
		
		String path = uri+(queryString==null?"":("?"+queryString));
		String redirect = requestUrl.replace(uri.equals("/")?"":uri, "");
		path = URLEncoder.encode(path, "UTF-8");
		redirect = URLEncoder.encode(redirect, "UTF-8");
//		String path = uri.replace(contentPath, "");//踢出掉工程名的相对路径，不含请求参数
//		if("/".equals(path))path="";
//		String fullRequestPath = request.getRequestURL().toString();//当前请求的完整路径
//		fullRequestPath = URLEncoder.encode(fullRequestPath, "UTF-8");
		
//		log.info(contentPath);
		log.info(uri);
//		log.info(path);
//		log.info("AjaxUtils.isAjax==>"+AjaxUtils.isAjax(request));
//		log.info("==========SecurityInterceptor==============");
		
		//如果是忽略的不需要匹配的路径，则直接返回true
		String ignorePath = sysProperties.getIgnore_path()+",/ssoauth,/login,/auth,/logout,/pages/login.html";
		String [] ignorePaths = ignorePath.split(",");
		AntPathMatcher mather = new AntPathMatcher();
		boolean isMather=false;
		for(String item:ignorePaths){
			if(StringUtils.isBlank(item)){
				continue;
			}
			isMather = mather.matchStart(item, matcherPath);
			if(isMather){
				return true;
			}
		}
		
//		String token = request.getHeader("Authorization");
		String token = null;
		Object tmp = CookieUtils.get(request, cookieHeadKey);
		if(tmp!=null){
			token = tmp.toString();
		}
		if(StringUtils.isBlank(token)){
			token = request.getHeader(cookieHeadKey);
		}
		
		//token为空
		if(StringUtils.isBlank(token)){
			if(AjaxUtils.isAjax(request)){
				write("401.a","Authorization不能为空",response);
				return false;
			}else{
//				response.sendRedirect(sysProperties.getSso_url()+ssoServerAuthUrl+"?redirect="+fullRequestPath+"&serverId="+sysProperties.getServer_id());
				response.sendRedirect(sysProperties.getSso_url()+ssoServerAuthUrl+"?callback="+redirect+"&path="+path);
				return false;
			}
		}
		
		//redis中token过期
		if(!redisUtils.hasKey(token)){
			if(AjaxUtils.isAjax(request)){
				write("401.b","Authorization已过期，请重新登录",response);
				return false;
			}else{
//				response.sendRedirect(sysProperties.getSso_url()+ssoServerAuthUrl+"?redirect="+fullRequestPath+"&serverId="+sysProperties.getServer_id());
				response.sendRedirect(sysProperties.getSso_url()+ssoServerAuthUrl+"?callback="+redirect+"&path="+path);
				return false;
			}
		}
		
		String userInfoToken = redisUtils.get(token)+"";
//		//解析jwtToken
//		JwtUserFactory factory = new JwtUserFactory();
//		Claims claims = factory.parseJwtToken(userInfoToken);
//		if(claims==null){
//			//理论上不存在此种if情况
//			write("401","令牌解析后内容为空",response);
//			return false;
//		}
//		Date issAt = claims.getIssuedAt();
//		String userId = claims.get("userId")+"";
//		Date lastPasswordUpdateTime = new Date();
		UserBO user = JSON.parseObject(userInfoToken, UserBO.class);
		Date lastPasswordUpdateTime = new Date();
		Date issAt = user.getIssAt();
		//密码的最后更新时间在签发令牌之后
		if(lastPasswordUpdateTime.getTime() > issAt.getTime() && false){//TODO:
			write("401.c","密码发生变化，请重新登录",response);
			return false;
		}else{
//			UserBO user = new UserBO();
//			for(String key:claims.keySet()){
//				BeanUtils.copyProperty(user, key, claims.get(key));
//			}
			//TODO:组装user对象放ThreadLocal中
			redisUtils.expire(token, timeout);
			
			//验证t_sys_permission表中的权限拦截
			boolean validate = validate(user,path,method);
			if(!validate){
		        String url = request.getRequestURL().toString();
//				log.error("用户{}-角色{}申请访问资源{}，权限不足",UserUtils.getUser().getUsername(),UserUtils.getUser().getRole().toArray(),url);
				
				write("401.d","您当前的权限或角色不足以访问该资源",response);
				return false;
			}
			
			UserUtils.setUser(user);
			
			return true;
		}
	}
	
	//返回信息到前台
	private void write(String code,String msg,HttpServletResponse response) throws UnsupportedEncodingException, IOException{
		JSONObject result = new JSONObject();
		result.put("code", code);
		result.put("msg", msg);
		response.setContentType("application/json; charset=utf-8");
//		PrintWriter pw = null;
		ServletOutputStream outputStream = response.getOutputStream();
		try {
//			pw = response.getWriter();
//			pw.write(result.toString());
			outputStream.write(JSONObject.toJSONString(result).getBytes("UTF-8"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(outputStream!=null){
				try {
//					pw.close();
					outputStream.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		UserUtils.clear();
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}
	
	//验证权限
	private boolean validate(UserBO user,String path,String method) throws UnsupportedEncodingException {
		
		//若yml文件配置了不启用 url 拦截权限校验，则放行
		if(!sysProperties.isEnableUrlIntercept())
			return true;
		//特殊用户角色，不拦截
		if(sysProperties.getExcludeUrlInterceptRole()!=null){
			for(Object role : sysProperties.getExcludeUrlInterceptRole()){
				for(String userRole : user.getRole()){
					if(role.equals(userRole)){
						return true;
					}
				}
			}
		}
		path = URLDecoder.decode(path, "UTF-8");
		List<String> requiredRoleList = this.sysPermissionDao.getSysPermissionJpa().select(path, method);
		
		//数据库中没有这部分的配置
		if(requiredRoleList==null || requiredRoleList.size()==0){
			return true;
		}
		
		List<String> userRoleList = user.getRole();
		for(String requiredRole:requiredRoleList){
			for(String userRole:userRoleList){
				if(userRole.equals(requiredRole.toString())){
					return true;
				}
			}
		}
		
		return false;
	}
	
}
