package me.springboot.fwk.security.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import me.springboot.fwk.log.Log2DB;
import me.springboot.fwk.redis.RedisUtils;
import me.springboot.fwk.utils.AjaxUtils;
import me.springboot.fwk.utils.CookieUtils;

@RestController
public class ServerAuthorizationController {

	private String cookieHeadKey="TOKEN_";
	private String cookieRememberMeKey="rememberMe";
	
	@Autowired
	private RedisUtils redisUtils;
	
	//业务系统登录
	@GetMapping(name="sso跳转到业务系统的回调地址",value="/auth")
	public void auth(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String path = request.getParameter("path");
		String token = request.getParameter(cookieHeadKey);
		String rememberMe = request.getParameter(cookieRememberMeKey);//记住我 30天免登陆
		
		if("1".equals(rememberMe)){
			CookieUtils.set(response, cookieHeadKey, token,30*24*60*60);
		}else{
			CookieUtils.set(response, cookieHeadKey, token);
		}
		if(StringUtils.isBlank(path)){
			path = "/";
		}
		response.sendRedirect(path);
	}
	
	
	//业务系统登出
	@GetMapping(name="用户登出",value="/logout")
	public void logout(HttpServletRequest request,HttpServletResponse response) throws IOException{
		Object cookieValue = CookieUtils.get(request, cookieHeadKey);
		if(cookieValue==null){
			cookieValue = request.getHeader(cookieHeadKey);
			if(cookieValue==null){
				return;
			}
		}
		CookieUtils.delete(response, cookieHeadKey);
		redisUtils.del(cookieValue+"");
		
		if(AjaxUtils.isAjax(request)){
			write("1","登出成功",response);
		}else{
			//返回根目录
			response.sendRedirect("");
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
