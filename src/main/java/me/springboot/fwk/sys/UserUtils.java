package me.springboot.fwk.sys;

public class UserUtils {

	private static final ThreadLocal<UserBO> threadLocal = new ThreadLocal<UserBO>();
	
	public static void setUser(UserBO user){
		threadLocal.set(user);
	}
	
	public static UserBO getUser(){
		return threadLocal.get();
	}
	
	public static void clear(){
		threadLocal.set(null);
	}
}
