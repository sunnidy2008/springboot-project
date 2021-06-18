package me.springboot.fwk.utils;

import javax.servlet.http.HttpServletRequest;

public class AjaxUtils {

	//判断是否是ajax请求
	public static boolean isAjax(HttpServletRequest request){
		String requestType = request.getHeader("X-Requested-With");
		if("XMLHttpRequest".equals(requestType)){
			return true;
		}else{
			return false;
		}
	}
}
