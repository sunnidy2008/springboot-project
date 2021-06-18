package me.springboot.fwk.security.annotation;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;
import me.springboot.fwk.exception.CustomException;
import me.springboot.fwk.properties.SysProperties;
import me.springboot.fwk.sys.UserUtils;

/**
 * redis 方案
 *
 * @author Levin
 * @since 2018/6/12 0012
 */
@Slf4j
@Aspect
@Configuration
public class HasRoleAspect {

	@Autowired
	private SysProperties sysProperties;
	
	@Around("execution(public * *(..)) && @annotation(me.springboot.fwk.security.annotation.HasRole)")
	public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {
		//若yml文件配置了不启用拦截权限校验，则放行
		if(!sysProperties.isEnableRoleIntercept())
			return true;
		
		//当前用户或者角色为空，直接返回false
		if(UserUtils.getUser()==null || UserUtils.getUser().getRole()==null){
			throw new CustomException(0, "您当前的权限或角色不足以访问该资源");
		}
		
		//特殊用户角色，不拦截
		if(sysProperties.getExcludeRoleInterceptRole()!=null){
			List<String> userRoleList = UserUtils.getUser().getRole();
			for(Object role : sysProperties.getExcludeRoleInterceptRole()){
				for(String userRole : userRoleList){
					if(role.equals(userRole)){
						return pjp.proceed();
					}
				}
			}
		}
		
		MethodSignature signature = (MethodSignature) pjp.getSignature();
		Method method = signature.getMethod();
		HasRole hasRole = method.getAnnotation(HasRole.class);
		String value = hasRole.value();

		if (value == null || "".equals(value)) {
			return pjp.proceed();
		}

		
		
		List<String> roles = UserUtils.getUser().getRole();

		String values[] = value.replaceAll("&&", ",").replaceAll("\\|\\|", ",").split(",");

		boolean contains = false;
		for (String item : values) {
			contains = contains(item.trim(), roles);
			value = value.replace(item, contains + "");
		}

		ExpressionParser parse = new SpelExpressionParser();
		// 判断2>1 返回boolean类型
		Expression expression = parse.parseExpression(value);
		Object result = expression.getValue();
		if (Boolean.valueOf(result.toString())) {
			return pjp.proceed();
		} else {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	        String url = request.getRequestURL().toString();
			log.error("用户{}申请访问资源{}，权限不足，用户拥有的权限为{}，需要的权限为{}",UserUtils.getUser().getUsername(),url,roles.toArray(),hasRole.value());
			throw new CustomException(0, "您当前的权限或角色不足以访问该资源");
		}
	}

	private boolean contains(String value, List<String> roles) {
		for (String item : roles) {
			if (item.equals(value)) {
				return true;
			}
		}

		return false;
	}
}