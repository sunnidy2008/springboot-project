package me.springboot.fwk.security;

import org.springframework.stereotype.Service;

import me.springboot.fwk.utils.BCryptUtils;

@Service
public class MyBCryptPasswordEncoder {

	
	/**
	 * 加密
	 * @param orgMsg 需要加密的信息
	 * @return
	 */
	public String encoder(String orgMsg){
		return BCryptUtils.hashpw(orgMsg, BCryptUtils.gensalt(12));
	}
	
	/**
	 * 判断是否一致
	 * @param orgMsg 原始信息
	 * @param encodedMsg 加密后的信息
	 * @return
	 */
	public boolean isMatch(String orgMsg,String encodedMsg){
		return BCryptUtils.checkpw(orgMsg, encodedMsg);
	}
}
