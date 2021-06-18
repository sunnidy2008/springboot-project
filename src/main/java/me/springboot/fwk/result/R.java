package me.springboot.fwk.result;

public class R {

	public static Result success(Object obj){
		Result r = new Result();
		r.setCode(1);
		if(obj instanceof java.lang.String){
			r.setMsg(obj.toString());
			r.setData("");
		}else{
			r.setMsg("操作成功");
			r.setData(obj);
		}

		return r;
	}
	
	public static Result error(Object obj){
		Result r = new Result();
		r.setCode(0);
		if(obj instanceof java.lang.String){
			r.setMsg(obj.toString());
			r.setData("");
		}else{
			r.setMsg("操作失败");
			r.setData(obj);
		}

		return r;
	}

	public static Result success(Object obj,String msg){
		Result r = new Result();
		r.setCode(1);
		r.setMsg(msg);
		r.setData(obj);
		return r;
	}
	
	public static Result success(){
		return success(null);
	}
	
	public static Result error(Integer code,String msg){
		Result r = new Result();
		r.setCode(code);
		r.setMsg(msg);
		return r;
	}
	
}
