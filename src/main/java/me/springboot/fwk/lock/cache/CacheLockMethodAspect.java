package me.springboot.fwk.lock.cache;

import java.lang.reflect.Method;
import java.util.UUID;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * redis 方案
 *
 * @author Levin
 * @since 2018/6/12 0012
 */
@Slf4j
@Aspect
@Configuration
public class CacheLockMethodAspect {

    @Autowired
    public CacheLockMethodAspect(RedisLockHelper redisLockHelper, CacheLockKeyGenerator cacheLockKeyGenerator) {
        this.redisLockHelper = redisLockHelper;
        this.cacheLockKeyGenerator = cacheLockKeyGenerator;
    }

    private final RedisLockHelper redisLockHelper;
    private final CacheLockKeyGenerator cacheLockKeyGenerator;


    @Around("execution(public * *(..)) && @annotation(me.springboot.fwk.lock.cache.CacheLock)")
    public Object interceptor(ProceedingJoinPoint pjp) throws Exception{
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        CacheLock lock = method.getAnnotation(CacheLock.class);
        if (StringUtils.isEmpty(lock.prefix())) {
            throw new RuntimeException("lock key don't null...");
        }
        String lockKey = cacheLockKeyGenerator.getLockKey(pjp);
        lockKey="cacheLock_"+lockKey;
        String value = UUID.randomUUID().toString();
        log.info(lockKey+"==>"+value);
        try {
            // 假设上锁成功，但是设置过期时间失效，以后拿到的都是 false
            final boolean success = redisLockHelper.lock(lockKey, value, lock.expire(), lock.timeUnit());
            if (!success) {
                throw new RuntimeException(lock.msg());
            }
            try {
                Object obj = pjp.proceed();
                return obj;
            } catch (Throwable throwable) {
                throw new RuntimeException("系统异常");
            }
        } finally {
            // TODO 如果演示的话需要注释该代码;实际应该放开
            redisLockHelper.unlock(lockKey, value);
        }
    }
}