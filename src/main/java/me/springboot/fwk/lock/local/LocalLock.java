package me.springboot.fwk.lock.local;

import java.lang.annotation.*;

/**
 * 锁的注解
 *
 * @author Levin
 */
//本地业务锁
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface LocalLock {

    /**
     * @author fly
     */
    String key() default "";
    
    /**
     * 重复提交时的提示
     * @return
     */
    String msg() default "重复提交";

    /**
     * 过期时间 TODO 由于用的 guava 暂时就忽略这属性吧 集成 redis 需要用到
     * 锁自动释放时间
     *
     * @author fly
     */
    int expire() default 30;
}