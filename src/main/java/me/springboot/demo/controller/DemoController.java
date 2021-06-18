package me.springboot.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;
import me.springboot.demo.dao.DemoRepository;
import me.springboot.demo.service.DemoService;
import me.springboot.demo.vo.DemoVO;
import me.springboot.fwk.db.DbUtils;
import me.springboot.fwk.exception.CustomException;
import me.springboot.fwk.limit.Limit;
import me.springboot.fwk.limit.LimitType;
import me.springboot.fwk.lock.cache.CacheLock;
import me.springboot.fwk.lock.cache.CacheLockParam;
import me.springboot.fwk.lock.local.LocalLock;
import me.springboot.fwk.log.Log2DB;
import me.springboot.fwk.properties.SysProperties;
import me.springboot.fwk.redis.RedisUtils;
import me.springboot.fwk.result.R;
import me.springboot.fwk.result.Result;
import me.springboot.fwk.security.annotation.HasRole;
import me.springboot.fwk.sys.UserBO;
import me.springboot.fwk.sys.UserUtils;
import me.springboot.fwk.websocket.server.WebSocketServer;
import me.springboot.fwk.websocket.service.WebsocketService;
import me.springboot.fwk.websocket.vo.SysWsMessageVO;

@RestController
@Slf4j
@EnableCaching
public class DemoController {

//	@Value("${sys.agreement}")
//	private String agreement;
	@Autowired
	private SysProperties p;
	@Autowired
	private DemoRepository demoRepository;
	@Autowired
	private RedisUtils ru;
	@Autowired
	private DemoService demoService;
	@Autowired
	private DbUtils dbUtils;
	@Autowired
	private WebsocketService websocketService;
	
	
	@PostMapping(name ="控制台log日志打印及log日志文件存储",value="/demo/log")
    public Result demo1() {
    	log.info(p.getDownloadPath());
    	
    	//日志相关
    	log.error("我是{}日志输出","error");
        log.info("我是{}日志输出","info");
        log.warn("我是{}日志输出","warn");

        return R.success();
    }
	
	@Log2DB(name="log日志存储到数据库")
	@PostMapping(name ="日志存储到数据库",value="/demo/log2db")
	public Result log2db(@RequestBody JSONObject params) throws Exception{
		UserBO user = UserUtils.getUser();
		log.info(user.getUsername());
		return R.success(user);
	}
	
	@Log2DB(name="log是单独的事务存储到数据库")
	@Transactional(rollbackFor = Exception.class)
	@PostMapping(name ="数据库操作",value="/demo/dbOperation")
	public Result dbOperation(@RequestBody JSONObject params1) throws Exception{
	     //保存数据
	     DemoVO demo = new DemoVO();
	     demo.setEmail("a@b.com");
	     demo.setPassword("123456");
	     demo.setUsername("张三"+System.currentTimeMillis());
	     this.demoRepository.save(demo);
	     
//	     if(true)throw new Exception("呃呃呃呃呃呃鹅鹅鹅鹅鹅鹅饿");
	     //按id查询数据
	     DemoVO demo1 = this.demoRepository.findById(30).get();
	     
	     //执行sql查询使用？作为占位符    不支持list和in参数
	     String sql1 = "select username as uname from t_demo where username=?";
	     List<Map<String,Object>> list1 = dbUtils.querySql(sql1, "zhangsan");
	     
	     
	     //执行sql查询使用:参数名 作为占位符，支持list和in参数
	     String sql2 = "select username as uname from t_demo where username in (':usernameList')";
	     Map<String,Object> params = new HashMap<String,Object>();
	     List<String> list = new ArrayList<String>();
	     list.add("李四");
	     list.add("王五");
	     params.put("usernameList", list);
	     List<Map<String,Object>> list2 = dbUtils.querySql(sql2, params);
	     
	     return R.success();
	}
	
	
	@PostMapping(name ="redis操作",value="/demo/redis")
	public Result redis(@RequestBody JSONObject param){
		//redis相关
		DemoVO demo = new DemoVO();
		demo.setEmail("a@b.com");
		demo.setPassword("123456");
		demo.setUsername("张三"+System.currentTimeMillis());
		ru.set("aaa", demo);
		DemoVO redisDemo = (DemoVO)ru.get("aaa");
		ru.del("bbb");
		List list = new ArrayList();
		list.add("ab");
		list.add("12");
		ru.lSet("ccc", list);
	     
	    return R.success();
	}
	
	
	/**
	 * 必须拥有指定权限才能访问，@HasRole注解，里面支持&&和||及小括号表达式
	 * @return
	 * @throws Exception
	 */
	@PostMapping(name="角色权限拦截",value="/demo/roleIntercept")
	@HasRole("admin")
	public Result roleIntercept(@RequestBody JSONObject params) throws Exception{
		UserBO user = UserUtils.getUser();
		return R.success(user);
	}
	
	@Log2DB(name="业务异常也能记录到数据库")
	@PostMapping(name="异常处理机制",value="/demo/exception")
    public Result exception(@RequestBody JSONObject params) {
	    if(true){
	    	throw new CustomException(300,"错误了");
	    }else{
	    	return null;
	    }
    }
	
	@PostMapping(name="分布式缓存，添加缓存到内容中",value="/demo/cacheable")
    public Result cacheable(@RequestBody JSONObject params) throws Exception {
	    DemoVO cacheDemo = this.demoService.get(30);
        return R.success(cacheDemo);
    }
	
	@PostMapping(name="分布式缓存，更新缓存",value="/demo/cachePut")
    public Result cachePut(@RequestBody JSONObject params) throws Exception {
	    DemoVO cacheDemo = this.demoService.get(30);
	    cacheDemo.setUsername("lisi");
	    this.demoService.saveOrUpdate(cacheDemo);
        return R.success(cacheDemo);
    }
	
	@PostMapping(name="分布式缓存，删除缓存",value="/demo/cacheEvict")
	public Object cacheEvict(@RequestBody JSONObject params) throws Exception {
		this.demoService.delete(30L);
		return R.success();
	}
	
	//本地缓存锁，若5秒内有相同参数的重复请求，则拒绝
	@LocalLock(key = "myKey:arg[0]",expire=5)//不管业务需要几秒完成，5秒收该锁将会被释放
	@PostMapping(name="本地缓存锁1（指定请求参数作为缓存key）",value="/demo/localLock1")
	public Result localLock(@RequestParam(value="a") String a) throws InterruptedException{
		//模拟业务需要3秒才完成
		//不管业务需要几秒完成，5秒收该锁将会被释放
		Thread.sleep(3*1000L);
		return R.success(a);
	}
	
	//本地缓存锁，若5秒内有相同参数的重复请求，则拒绝
	@PostMapping(name="本地缓存锁2（指定缓存名称）",value="/demo/localLock2")
	@LocalLock(key = "test",expire=5)
	public Result demo7(@RequestBody JSONObject param) throws Exception{
//		List paramList = new ArrayList();
//		paramList.add("lisi");
//		paramList.add("wangwu");
//		
//		String sql = "select id as userid,username as username from t_demo where username like '%#username%' or username in ('#userlist')";
//		JSONObject params = new JSONObject();
//		params.put("username", "lisi");
//		params.put("userlist", paramList);
//		params.put("_pageNum", 1);
//		params.put("_pageSize", 10);
//		PageResult pageResult = dbUtils.queryPageSql(sql, params);
//		
//		String sql1 = "select * from t_demo where username in (?)";
//		List list = dbUtils.querySql(sql1, paramList);
		Thread.sleep(3*1000L);
		return R.success();
	}
	
	//分布式缓存锁，若5秒内有相同参数的重复请求，则拒绝
	@CacheLock(prefix = "cacheLock",msg="系统繁忙，请稍后重试")
	@PostMapping(name="分布式缓存锁(依赖redis)",value="/demo/cacheLock")
	public Result cacheLock(@CacheLockParam(name = "a")String a,@RequestParam(value="b") String b) throws InterruptedException{
		//模拟业务需要3秒才完成
		//不管业务需要几秒完成，5秒收该锁将会被释放
		log.info(b+"################");
		Thread.sleep(3*1000l);
		log.info(b+"================");
		return R.success(a);
	}
	
	//限流
	@PostMapping(name="限流",value="/demo/limit")
	@Limit(period = 10, count = 3,limitType=LimitType.METHOD)//意味著所有ip在 10S 内最多允許訪問该方法3次
//	@Limit(key="test",period = 10, count = 3)//意味著 10S所有ip 内最多允許訪問资源名为“test”的资源3次
//	@Limit(period = 10, count = 3,limitType=LimitType.IP)//意味著 指定ip10秒内最多访问带有limit标签的资源3次
//	@Limit(period = 10, count = 3,limitType=LimitType.IP_METHOD)//意味著 指定ip和方法10秒内最多访问该资源3次
	public Result limit(@RequestBody JSONObject params) throws Exception{
		UserBO user = UserUtils.getUser();
		log.info(user.getUsername());
		log.info("这个只是测试limit限流而已");
		return R.success(user);
	}
	
	//websocket
	@PostMapping(name="websocket通讯",value="/demo/websocket")
	public Result websocket(@RequestBody JSONObject params) throws Exception{
//		WebSocketServer.sendInfo("这个是一条来自服务器推送过来的消息","20");
		SysWsMessageVO bean = new SysWsMessageVO();
		bean.setcToUserId(UserUtils.getUser().getId());
		bean.setcText("从服务器推送过来的消息，点击打开百度从服务器推送过来的消息，点击打开百度");
		bean.setcUrl("http://www.baidu.com?A=1");
		bean.setcExtraData("这里是额外附带的参数");
		websocketService.sendMsg(bean);
		return R.success();
	}
	
	
	@Log2DB(name="log是单独的事务存储到数据库")
//	@Transactional(rollbackFor = Exception.class)
	@PostMapping(name ="数据库操作",value="/demo/dbOperation1")
	public Result dbOperation1(@RequestBody JSONObject params1) throws Exception{
	     
//		save1();
//		save2();
		insert1();
		insert2();
	     
//		DemoVO demo = new DemoVO();
//	     demo.setEmail("2222222222");
//	     demo.setPassword("22222222222222222");
//	     demo.setUsername("张三"+System.currentTimeMillis());
//	     this.demoRepository.save(demo);
////	     int i=1/0;
//	     if(true)throw new Exception("test");
	     
	     return R.success();
	}
	
	@Transactional
	public void save1(){
		//保存数据
	     DemoVO demo = new DemoVO();
	     demo.setEmail("1111111111111");
	     demo.setPassword("1111111111111111");
	     demo.setUsername("张三"+System.currentTimeMillis());
	     
	     this.demoRepository.save(demo);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void save2() throws Exception{
		//保存数据
		
	     DemoVO demo = new DemoVO();
	     demo.setEmail("2222222222");
	     demo.setPassword("22222222222222222");
	     demo.setUsername("张三"+System.currentTimeMillis());
	     this.demoRepository.save(demo);
//	     int i=1/0;
	     if(true)throw new Exception("test");
	}
	
	public void insert1(){
		dbUtils.updateSql("insert into t_demo  (email,password,username) values (?,?,?)", "aaa","aaaa","aaa");
	}
	public void insert2() throws Exception{
		dbUtils.updateSql("insert into t_demo  (email,password,username) values (?,?,?)", "bbbb","bbbbbb","bbbbb");
		throw new Exception("bbbbb");
	}
	
	class Author {
        private int age;
        private String name;
        private String email;
		// 省略 get set
		public int getAge() {
			return age;
		}
		public void setAge(int age) {
			this.age = age;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
        
    }
	
//	@PostMapping("/index2")
////	@PreAuthorize("hasAuthority('read')")
//    public ModelAndView index() {
//        ModelAndView view = new ModelAndView();
//        // 设置跳转的视图 默认映射到 src/main/resources/templates/{viewName}.html
//        view.setViewName("indexx");
////        Object user = UserUtils.getUser();
//        // 设置属性
//        view.addObject("title", "我的第一个WEB页面");
//        view.addObject("desc", "欢迎进入battcn-web 系统");
//        Author author = new Author();
//        author.setAge(22);
//        author.setEmail("1837307557@qq.com");
//        author.setName("唐亚峰");
//        view.addObject("author", author);
//        return view;
//    }
//	@PostMapping("/home2")
////	@PreAuthorize("hasAuthority('read')")
//	public ModelAndView home() {
//		ModelAndView view = new ModelAndView();
//		// 设置跳转的视图 默认映射到 src/main/resources/templates/{viewName}.html
//		view.setViewName("home");
//		return view;
//	}
	
//	@PostMapping("/ssologinPage")
////	@PreAuthorize("hasAuthority('read')")
//	public ModelAndView ssologinPage() {
//		ModelAndView view = new ModelAndView();
//		// 设置跳转的视图 默认映射到 src/main/resources/templates/{viewName}.html
//		view.setViewName("ssologin");
//		return view;
//	}

//	@PreAuthorize("hasAuthority('read1')")
//    @PostMapping("/index1")
//    public String index1(HttpServletRequest request) {
//        // TODO 与上面的写法不同，但是结果一致。
//        // 设置属性
//        request.setAttribute("title", "我的第一个WEB页面");
//        request.setAttribute("desc", "欢迎进入battcn-web 系统");
//        Author author = new Author();
//        author.setAge(22);
//        author.setEmail("1837307557@qq.com");
//        author.setName("唐亚峰");
//        request.setAttribute("author", author);
//        // 返回的 index 默认映射到 src/main/resources/templates/xxxx.html
//        return "indexx";
//    }
    
//    @PostMapping("/user")
//    public Authentication user(Authentication user) {
////        SecurityContextHolder.getContext().getAuthentication();
////        MyUser user1 = (MyUser)user;
//    	OAuth2AuthenticationDetails myToken = (OAuth2AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
//    	String tv = myToken.getTokenValue();
//    	String sId = myToken.getSessionId();
//    	System.out.println(tv);
//    	System.out.println(sId);
//        return user;
//    }
//    
//    @PostMapping("/me")
//    public Object me() {
////        SecurityContextHolder.getContext().getAuthentication();
////        MyUser user1 = (MyUser)user;
//        return UserUtils.getUser();
//    }
    
//    @PostMapping("/logout")
//    public void logout(HttpServletRequest request,HttpServletResponse response) throws IOException {
////        SecurityContextHolder.getContext().getAuthentication();
////        MyUser user1 = (MyUser)user;
////    	Cookie c = new Cookie("JSESSIONID","");
////		c.setMaxAge(0); //设置生存期为24小时
////		response.addCookie(c);
////    	response.sendRedirect("user");
//    	
//    	new SecurityContextLogoutHandler().logout(request, null, null);
//    	response.sendRedirect("user");
//    }
}
