package me.springboot.demo.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 基于 Spring 自带的
 *
 * @author Levin
 * @since 2018/5/29 0029
 */
@Component
@Slf4j	
public class SpringTaskDemo {

//    @Async
//    @Scheduled(cron = "0/1 * * * * *")
//    public void scheduled1() throws InterruptedException {
//        Thread.sleep(3000);
//        log.info("scheduled1 每1秒执行一次：{}", LocalDateTime.now());
//    }
//
//    @Scheduled(fixedRate = 1000)
//    public void scheduled2() throws InterruptedException {
//        Thread.sleep(3000);
//        log.info("scheduled2 每1秒执行一次：{}", LocalDateTime.now());
//    }
//
//    @Scheduled(fixedDelay = 3000)
//    public void scheduled3() throws InterruptedException {
//        Thread.sleep(5000);
//        log.info("scheduled3 上次执行完毕后隔3秒继续执行：{}", LocalDateTime.now());
//    }

}