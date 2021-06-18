package me.springboot.fwk.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import me.springboot.fwk.exception.CustomException;
import me.springboot.fwk.result.R;
import me.springboot.fwk.result.Result;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(value=Exception.class)
	@ResponseBody
	public Result handle(Exception e){
		e.printStackTrace();
		if(e instanceof CustomException){
			CustomException ex = (CustomException)e;
			return R.error(ex.getCode(), ex.getMessage());
		}else{
			return R.error(0,e.getMessage());
		}
	}
}
