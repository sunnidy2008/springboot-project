package me.springboot.fwk.security.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import me.springboot.fwk.log.Log2DB;
import me.springboot.fwk.properties.SysProperties;
import me.springboot.fwk.redis.RedisUtils;
import me.springboot.fwk.security.MyBCryptPasswordEncoder;
import me.springboot.fwk.sys.UserBO;
import me.springboot.fwk.sys.UserUtils;
import me.springboot.fwk.sys.service.UserDetailsService;
import me.springboot.fwk.utils.AjaxUtils;
import me.springboot.fwk.utils.CookieUtils;

@RestController
public class SsoAuthorizationController {

	private String cookieHeadKey="TOKEN_";
	private String redisTokenKeyPrefix="TOKEN_";
	private String ssologinPage = "pages/login.html";
	private Long timeout=30*60L;//延长30分钟
	
	@Autowired
	private RedisUtils redisUtils;
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private MyBCryptPasswordEncoder passwordEncoder;
	@Autowired
	private SysProperties sysProperties;
	@Bean(name="passwordEncoder")
	public MyBCryptPasswordEncoder passwordEncoder() {
		return new MyBCryptPasswordEncoder();
	}
	
	//sso认证中心
	@GetMapping(name="业务系统请求sso判断用户是否已登录的地址",value="/ssoauth")
	public void auth(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String callback = request.getParameter("callback");
		String path = request.getParameter("path");
//		callback = URLDecoder.decode(callback, "UTF-8");
//		path = URLDecoder.decode(path, "UTF-8");
				
//		String serverId = request.getParameter("serverId");
		Object token = CookieUtils.get(request, cookieHeadKey);
		callback = URLEncoder.encode(callback, "UTF-8");
		path = URLEncoder.encode(path, "UTF-8");
//		if(!redisUtils.hasKey(serverId)){
//			write("0","reidis中缺少参数serverId",response);
//			return;
//		}
		
//		String serverUrl = redisUtils.get(serverId).toString();
		//token不为空
		if(token!=null){
			if(redisUtils.hasKey(token.toString())){
				//有实际跳转的业务系统路径，跳回去
				if(callback!=null){
					//TODO:
					callback = callback+"/auth?path="+path+"&"+cookieHeadKey+"="+token;
					response.sendRedirect(callback);
				}
				//没有实际跳转的业务系统路径，跳转到sso的默认主页
				else{
					response.sendRedirect("");
				}
			}else{
//				request.getSession().setAttribute("redirect", redirect);
//				request.getSession().setAttribute("serverId", serverId);
				response.sendRedirect(ssologinPage+"?callback="+callback+"&path="+path);
			}
		}
		//token为空，需要登录，条状到登录界面
		else{
//			request.getSession().setAttribute("redirect", redirect);
//			request.getSession().setAttribute("serverId", serverId);
			response.sendRedirect(ssologinPage+"?callback="+callback+"&path="+path);
		}
	}
	
	//sso用户登录
	@PostMapping(name="登录",value="/login")
	@Log2DB(name="登录")
	public void login(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String rememberMe = request.getParameter("rememberMe");//30天免登陆，1是，0否
		if(rememberMe==null){
			rememberMe="0";
		}
		
		//TODO:验证用户名密码有效性
		
		UserBO user = null;
		try {
			user = this.userDetailsService.loadUserByUsername(username);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		
		String callback = request.getParameter("callback");
		String path = request.getParameter("path");
		if(callback==null){
			callback = "/";
		}
		if(path==null){
			path = "";
		}
//		String serverId = request.getParameter("serverId");
//		if(serverId==null && !AjaxUtils.isAjax(request)){
//			write("0","缺少参数serverId",response);
//			return;
//		}
		
		if(user==null){
			if(AjaxUtils.isAjax(request)){
				write("0","用户不存在",response);
			}else{
				callback = URLEncoder.encode(callback, "UTF-8");
				response.sendRedirect(ssologinPage+"?callback="+callback+"&path="+path+"&error=noUser");
				return;
			}
		}
		
		if(!passwordEncoder.isMatch(password, user.getPassword())){
			if(AjaxUtils.isAjax(request)){
				write("0","密码错误",response);
			}else{
				callback = URLEncoder.encode(callback, "UTF-8");
				response.sendRedirect(ssologinPage+"?callback="+callback+"&path="+path+"&error=passwordNotMatch");
				return;
			}
		}
		
//		JwtUserFactory factory = new JwtUserFactory();
//		String jString = JSON.toJSONString(user);
//		UserBO u = JSON.parseObject(jString, UserBO.class);
//		String token = factory.getJwtToken(user);
		//将密码置空，否则会存放到redis中，也会放到ThreadLocal里面去，可能返回到前台，不合适
		user.setPassword(null);
		String token = JSON.toJSONString(user);
		String key = redisTokenKeyPrefix+UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
		
		if("1".equals(rememberMe)){
			redisUtils.set(key, token, 30*24*60*60L);//30天免登陆
			CookieUtils.set(response, cookieHeadKey, key,30*24*60*60);
		}else{
			redisUtils.set(key, token, timeout);
			CookieUtils.set(response, cookieHeadKey, key);
		}
		
		UserUtils.setUser(user);
		if(AjaxUtils.isAjax(request)){
			write("1",key,response);
			return;
		}else{
//			if(!redisUtils.hasKey(serverId)){
//				write("0","缺少参数serverId",response);
//				return;
//			}
//			String serverUrl = redisUtils.get(serverId).toString();
			
			callback = callback+"/auth?path="+URLEncoder.encode(path,"UTF-8")+"&"+cookieHeadKey+"="+key+"&rememberMe="+rememberMe;
			response.sendRedirect(callback);
		}
	}
	
	//返回信息到前台
	private void write(String code,String msg,HttpServletResponse response) throws UnsupportedEncodingException, IOException{
		JSONObject result = new JSONObject();
		result.put("code", code);
		result.put("msg", msg);
		response.setContentType("application/json; charset=utf-8");
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			pw.write(result.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(pw!=null){
				try {
					pw.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
