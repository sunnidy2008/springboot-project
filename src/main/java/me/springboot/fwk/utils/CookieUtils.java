package me.springboot.fwk.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {

	//根据key返回cookie中的value指
	public static Object get(HttpServletRequest request,String key){
		Cookie[] cookies = request.getCookies();
		if(cookies==null){
			return null;
		}
		for(Cookie c : cookies){
			if(c.getName().toUpperCase().equals(key)){
				return c.getValue();
			}
		}
		return null;
	}
	
	//设置一个cookie
	public static void set(HttpServletResponse response,String key,String value){
		Cookie cookie = new Cookie(key, value);
		cookie.setPath("/");
		response.addCookie(cookie);
	}
	
	public static void set(HttpServletResponse response,String key,String value,int maxAge){
		Cookie cookie = new Cookie(key, value);
		cookie.setPath("/");
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}
	
	//设置一个cookie
	public static void delete(HttpServletResponse response,String key){
		Cookie cookie = new Cookie(key, "");
		cookie.setPath("/");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}
}
