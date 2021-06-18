package me.springboot.fwk.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VoUtils {

	
	public static void touchOnCreate(Object obj,Date date,Object userId){
		try {
			BeanUtils.setProperty(obj, "tCrtTm", date);
			BeanUtils.setProperty(obj, "tUpdTm", date);
			BeanUtils.setProperty(obj, "cCrtId", userId);
			BeanUtils.setProperty(obj, "cUpdId", userId);
		} catch (IllegalAccessException e) {
			log.warn("设置对象"+obj+"的创建人/时间，修改人/时间出错!");
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (InvocationTargetException e) {
			log.warn("设置对象"+obj+"的创建人/时间，修改人/时间出错!");
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	
	public static void touchOnUpdate(Object obj,Date date,Object userId){
		try {
			BeanUtils.setProperty(obj, "tUpdTm", date);
			BeanUtils.setProperty(obj, "cUpdId", userId);
		} catch (IllegalAccessException e) {
			log.warn("设置对象"+obj+"的创建人/时间，修改人/时间出错1!");
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (InvocationTargetException e) {
			log.warn("设置对象"+obj+"的创建人/时间，修改人/时间出错2!");
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	
	
	public static void touch(Object obj,Date date,Object userId){
		try {
			if(BeanUtils.getProperty(obj, "tCrtTm")==null||BeanUtils.getProperty(obj, "tUpdTm")==null){
				VoUtils.touchOnCreate(obj, date, userId);
			}else{
				VoUtils.touchOnUpdate(obj, date, userId);
			}
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}
	
}
