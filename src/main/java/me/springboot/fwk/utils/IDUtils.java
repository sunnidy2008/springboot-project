package me.springboot.fwk.utils;

import java.util.UUID;

public class IDUtils {

    /**
     * 获取32位的UUID,不去除"-"
     * @return
     */
    public static String getUUID32(){
    	return UUID.randomUUID().toString();
    }

//    /**
//     * 获取32位的UUID,去除"-"
//     * @return
//     */
//    public static String getUUID32Reduce(){
//        String uuid=UUID.randomUUID().toString().replace("-","").toLowerCase().trim();
//        return uuid;
//    }

    public static void main(String[] args){

    }
}
