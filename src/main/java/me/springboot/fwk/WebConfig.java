package me.springboot.fwk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import me.springboot.fwk.properties.SysProperties;
import me.springboot.fwk.security.SecurityInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private SecurityInterceptor securityInterceptor;
    @Autowired
    private SysProperties sysProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(securityInterceptor).addPathPatterns("/**");
    }

    @Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/files/**").addResourceLocations("file:"+sysProperties.getFilesPath());
		registry.addResourceHandler("/download/**").addResourceLocations("file:"+sysProperties.getDownloadPath());
		registry.addResourceHandler("/upload/**").addResourceLocations("file:"+sysProperties.getUploadPath());
	}
    
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
//		registry.addViewController( "/" ).setViewName( "pages/index.html" );
		registry.addRedirectViewController("/", "pages/index.html" );
        registry.setOrder( Ordered.HIGHEST_PRECEDENCE );
        WebMvcConfigurer.super.addViewControllers(registry);
	}
}