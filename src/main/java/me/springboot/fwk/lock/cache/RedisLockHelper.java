package me.springboot.fwk.lock.cache;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.util.StringUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * 需要定义成 Bean
 *
 * @author Levin
 * @since 2018/6/15 0015
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisLockHelper {


    private static final String DELIMITER = "|";

    /**
     * 如果要求比较高可以通过注入的方式分配
     */
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(10);

    private final StringRedisTemplate stringRedisTemplate;

    public RedisLockHelper(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

//    /**
//     * 获取锁（存在死锁风险）
//     *
//     * @param lockKey lockKey
//     * @param value   value
//     * @param time    超时时间
//     * @param unit    过期单位
//     * @return true or false
//     */
//    public boolean tryLock(final String lockKey, final String value, final long time, final TimeUnit unit) {
//        return stringRedisTemplate.execute((RedisCallback<Boolean>) connection -> connection.set(lockKey.getBytes(), value.getBytes(), Expiration.from(time, unit), RedisStringCommands.SetOption.SET_IF_ABSENT));
//    }

    /**
     * 获取锁
     *
     * @param lockKey lockKey
     * @param uuid    UUID
     * @param timeout 超时时间
     * @param unit    过期单位
     * @return true or false
     */
    public boolean lock(String lockKey, final String uuid, long timeout, final TimeUnit unit) {
        final long milliseconds = Expiration.from(timeout, unit).getExpirationTimeInMilliseconds();
        //setIfAbsent相当于jedis中的setnx，如果能赋值就返回true，如果已经有值了，就返回false
        //即：在判断这个key是不是第一次进入这个方法
        boolean success = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, (System.currentTimeMillis() + milliseconds) + DELIMITER + uuid,timeout, TimeUnit.SECONDS);
        if (success) {
        	//第一次，即：这个key还没有被赋值的时候
        	return true;
        }else{
        	//超时了
        	String currentValue = stringRedisTemplate.opsForValue().get(lockKey);//A
        	if (Long.parseLong(currentValue.split(Pattern.quote(DELIMITER))[0]) + 1 <= System.currentTimeMillis()) {
        		String oldVal = stringRedisTemplate.opsForValue().getAndSet(lockKey, (System.currentTimeMillis() + milliseconds) + DELIMITER + uuid);//B
        		//如果两个线程同时调用这个方法，当同时走到A的时候，无论怎么样都有一个线程会先执行B这一行，假设线程1先执行B这行代码，
        		//那redis中key对应的value就变成了value，然后线程2再执行B这行代码的时候，获取到的old_value就是value，
        		//那么value显然和他上面获取的current_value是不一样的，则线程2是没法获取锁的。
                if(currentValue!=null && currentValue.equals(oldVal)){
                	return true;
                }
            }
        }
        return false;
    }


    /**
     * @see <a href="http://redis.io/commands/set">Redis Documentation: SET</a>
     */
    public void unlock(String lockKey, String value) {
        unlock(lockKey, value, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * 延迟unlock
     *
     * @param lockKey   key
     * @param uuid      client(最好是唯一键的)
     * @param delayTime 延迟时间
     * @param unit      时间单位
     */
    public void unlock(final String lockKey, final String uuid, long delayTime, TimeUnit unit) {
        if (StringUtils.isEmpty(lockKey)) {
            return;
        }
        if (delayTime <= 0) {
            doUnlock(lockKey, uuid);
        } else {
            EXECUTOR_SERVICE.schedule(() -> doUnlock(lockKey, uuid), delayTime, unit);
        }
    }

    /**
     * @param lockKey key
     * @param uuid    client(最好是唯一键的)
     */
    private void doUnlock(final String lockKey, final String uuid) {
        String val = stringRedisTemplate.opsForValue().get(lockKey);
        //有可能该锁到期后自动释放了
        if(val==null){
        	return;
        }
        final String[] values = val.split(Pattern.quote(DELIMITER));
        if (values.length <= 0) {
            return;
        }
        if (uuid.equals(values[1])) {
//        	stringRedisTemplate.multi();
            stringRedisTemplate.delete(lockKey);
//            stringRedisTemplate.exec();
        }
    }

}