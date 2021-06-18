package me.springboot.fwk.utils;

import java.util.Random;

/**
 * Created by Administrator on 2018/3/20.
 */
public class RandomUtils {
    /**
     * 获取随机位数的字符串
     *
     * @author fengshuonan
     * @Date 2017/8/24 14:09
     */
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        return getRandom(base,length);
    }

    public static String getRandomNum(int length){
        String base = "0123456789";
        return getRandom(base,length);
    }


    public static String getRandom(String base,int length){
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    
    /**
     * 获取一个最小到最大之间的随机数
     * @param min
     * @param max
     * @return
     */
    public static Long getRandom(long min,long max){
    	return (long)(Math.random()*(max-min+1)+max);
    }
}
