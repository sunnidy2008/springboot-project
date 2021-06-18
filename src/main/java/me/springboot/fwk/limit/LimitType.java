package me.springboot.fwk.limit;
public enum LimitType {
    /**
     * 自定义key
     */
    CUSTOMER,
    /**
     * 根据请求者IP
     */
    IP,
    /**
     * 根据方法名限流（典型应用场景是秒杀)
     */
    METHOD,
    /**
     * 限定指定ip访问指定method的频次
     */
    IP_METHOD;
}