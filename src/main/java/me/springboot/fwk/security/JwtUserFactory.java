package me.springboot.fwk.security;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import me.springboot.fwk.sys.UserBO;
import me.springboot.fwk.utils.BeanUtils;

public class JwtUserFactory {

	private String SECRET = "me.springboot.skywalker819";
	private int expired = 24*60*60*1000;//30天内有效

	public String getJwtToken1(UserBO user) throws Exception {
		Date now = new Date();
		// expire time
		Calendar nowTime = Calendar.getInstance();
		nowTime.setTime(now);
		nowTime.add(Calendar.SECOND, expired);
		Date expiresDate = nowTime.getTime();
		Claims claims = Jwts.claims();
		Map<String,Object> map = new HashMap<String,Object>();
		BeanUtils.bean2Map(user, map);
		Map<String,Object> finalMap=new HashMap<String,Object>();
		for(String key:map.keySet()){
			if(map.get(key)!=null && !"".equals(map.get(key))){
				finalMap.put(key, map.get(key));
			}
		}
		claims.putAll(finalMap);
		String token = Jwts.builder()
								.setClaims(claims)
								.setIssuedAt(now)
								.setExpiration(expiresDate)//jwt的签发时间
								.signWith(SignatureAlgorithm.HS512, SECRET)//设置签名使用的签名算法和签名使用的秘钥
								.compact();
		return token;
	}
	
	public Claims parseJwtToken1(String token) {
        Jws<Claims> jws = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
//        String signature = jws.getSignature();
//        Map<String, String> header = jws.getHeader();
//        for(String key : header.keySet()){
//        	System.out.println(key+"-->"+header.get(key));
//        }
        Claims Claims = jws.getBody();
        return Claims;
    }
	
	public static void main(String args[]){
//		JwtUserFactory p = new JwtUserFactory();
//		String token = p.getJwtToken("张三");
//		System.out.println(token);
//		
//		Claims claims = p.parseJwtToken(token);
//		System.out.println(claims.getIssuedAt());
//		for(String key:claims.keySet()){
//			System.out.println(key+"==>"+claims.get(key));
//		}
	}
}
