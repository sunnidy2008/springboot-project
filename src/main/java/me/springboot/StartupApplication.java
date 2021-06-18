package me.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import lombok.extern.slf4j.Slf4j;

/**
 * 我的第一个SpringBoot程序
 * 其中 @RestController 等同于 （@Controller 与 @ResponseBody）
 *
 * @author Levin
 */
@RestController
@SpringBootApplication
@Slf4j//logback日志
@EnableCaching//运行使用缓存
@EnableAsync//代表该任务可以进行异步工作，由原本的串行改为并行
@EnableScheduling//开启定时任务
//@EnableTransactionManagement
public class StartupApplication  extends SpringBootServletInitializer{

	
    public static void main(String[] args)  {
        SpringApplication.run(StartupApplication.class, args);
    }
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(StartupApplication.class);
    }
    
//    @Bean
//    public CacheLockKeyGenerator cacheLockKeyGenerator() {
//        return new CacheLockKeyGenerator();
//    }
    
    
    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
//        corsConfiguration.addExposedHeader(HttpHeaderConStant.X_TOTAL_COUNT);
        return corsConfiguration;
    }

    /**
     * 跨域过滤器
     *
     * @return
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig()); // 4
        return new CorsFilter(source);
    }

//    @Bean
//    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
//        // 目的是
//        return args -> {
//            System.out.println("来看看 SpringBoot 默认为我们提供的 Bean：");
//            String[] beanNames = ctx.getBeanDefinitionNames();
//            Arrays.sort(beanNames);
//            Arrays.stream(beanNames).forEach(System.out::println);
//        };
//    }
}
