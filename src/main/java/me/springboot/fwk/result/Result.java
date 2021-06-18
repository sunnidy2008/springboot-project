package me.springboot.fwk.result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import me.springboot.fwk.utils.BeanUtils;

/**
 * http请求返回的最外层对象
 * @author Administrator
 * @param <T>
 *
 */
public class Result<T> {

	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	private Integer code;
	private String msg;
	private T data;//具体内容

	@Override
	public String toString() {
		return "Result{" +
				"code=" + code +
				", msg='" + msg + '\'' +
				", data=" + data2String(data) +
				'}';
	}

	private String data2String(T data){
		if(data==null){
			return "";
		}
		if(data instanceof JSONObject || data instanceof Integer || data instanceof String
				|| data instanceof Double || data instanceof Float || data instanceof Character){
			return data.toString();
		}else if(data instanceof Map){
			String result = "";
			for(Object key : ((Map) data).keySet()){
				result =result+ key+":"+((Map) data).get(key)+";";
			}
			return result;
		}else if(data instanceof List){
			List tmp = (List)data;
			String result = "";
			for(int i=0;i<tmp.size();i++){
				result = result+tmp.get(i)+";";
			}
			return result;
		}else{
			Map tmp = new HashMap();
			try {
				BeanUtils.bean2Map(data,tmp);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return data2String((T) tmp);
		}
	}
}
