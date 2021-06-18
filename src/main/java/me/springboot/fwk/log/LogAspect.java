package me.springboot.fwk.log;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.connector.RequestFacade;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;
import me.springboot.fwk.sys.UserUtils;

@Aspect
@Configuration
@Slf4j
public class LogAspect {


	@Autowired
	private LogService logService;
	
	//在com.hneb 下的所有类之前执行doBefre 之后执行doAfter
//	@Pointcut("execution(public * *(..)) && @annotation(me.springboot.fwk.log.Log2DB)")
//	public void logs(){
//		
//	}
	
	@Around("execution(public * *(..)) && @annotation(me.springboot.fwk.log.Log2DB)")
	public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Log2DB logAnnotation = signature.getMethod().getAnnotation(Log2DB.class);
        if(!logAnnotation.isLog()){
        	return joinPoint.proceed();
        }
		
		ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		if(attributes==null){
			return joinPoint.proceed();
		}
		HttpServletRequest request = attributes.getRequest();

		String url,method,clientIp,clazz,requestData="",userId="",serverIp,serverPort,name;
		Object responseData=null;
		Boolean success = true;
		long took = 0;

		name = logAnnotation.name();
		url = request.getRequestURL().toString();
		serverIp = request.getLocalAddr();
		serverPort = request.getLocalPort()+"";
		method = request.getMethod();
		clientIp =request.getRemoteAddr();
		clazz =joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName();
		Object args []=joinPoint.getArgs();
		for(int i=0;i<args.length;i++){
			if(args[i] instanceof org.apache.catalina.connector.RequestFacade){
				RequestFacade rf = (RequestFacade)args[i];
				Map<String,String[]> parameterMap = rf.getParameterMap();
				for(String key : parameterMap.keySet()){
					requestData = requestData + key+"="+parameterMap.get(key)[0]+"&";
				}
				if(requestData.length()!=0){
					requestData = requestData.substring(0,requestData.length()-1);
				}
				break;
			}else{
				requestData =requestData+args[i].toString()+",";
			}
		}

//		Boolean requireLogin = isLoginRequired(((MethodSignature)joinPoint.getSignature()).getMethod());
//		//如果需要用户登录，但没有当前用户信息，则返回
//		if(requireLogin){
//			if(CurrentUser.getUser()==null){
//				return ResultUtils.error(ResultEnum.SYS_REQUIRE_USER.getCode(),ResultEnum.SYS_REQUIRE_USER.getMsg());
//			}else{
//				userId = CurrentUser.getUser().getUserId();
//			}
//		}else{
//			userId = "anonymous";
//		}
//		log.info("当前方法是否需要验证登录:"+requireLogin);

		

		//记录起始时间
		Date begin = new Date();
		Date end = null;
		/** 执行目标方法 */
		try{
			responseData= joinPoint.proceed();
			log.info("response={}",responseData==null?"":responseData.toString());
		}
		catch(Exception e){
			success=false;
			responseData = e.getMessage();
			log.error("errorMessage: {}", e.getMessage());
			e.printStackTrace();
			throw e;
		}
		finally{
			//必须在执行完业务方法后获取当前用户id，否则登录请求拿不到用户数据
			if(UserUtils.getUser()!=null){
				userId = UserUtils.getUser().getId();
			}
			
			end = new Date();
			/** 记录操作时间 */
			took = (end.getTime() - begin.getTime());

//			//url
//			log.info("url={}",url);
//			log.info("serverId={}",serverIp);
//			//method
//			log.info("userId={}",userId);
//			log.info("method={}",method);
//			//ip
//			log.info("ip={}",clientIp);
//			//类方法
//			log.info("class_method={}",clazz);
//			//参数
//			log.info("requestData={}",requestData);
//			log.info("responseData={}",responseData);
//			//是否成功
//			log.info("success={}",success);
//			if (took >= 10*1000) {
//				log.error("took={}", took);
//			} else if (took >= 5*1000) {
//				log.warn("took={}", took);
//			} else{
//				log.info("took={}", took);
//			}

			this.logService.save(name,userId, clientIp,serverIp,serverPort, begin, end, took, method, clazz, requestData.toString(), responseData==null?"":responseData.toString(), url, success.toString());
		}
		return responseData;
	}

//	@Before("logs()")
//	public void doBefore(JoinPoint point){
////		log.info("do before");
//	}
//	
//	@After("logs()")
//	public void doAfter(){
////		log.info("do after");
//	}
//	
//	//在doAfter之后执行，主要用户记录程序执行后的返回值
//	@AfterReturning(returning="object",pointcut="logs()")
//	public void doAfterReturning(Object object){
////		log.info("do afterReturning");
//	}
}
