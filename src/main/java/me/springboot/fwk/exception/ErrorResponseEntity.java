package me.springboot.fwk.exception;

/**
 * @author Levin
 * @since 2018/6/1 0001
 */
public class ErrorResponseEntity {

    private int code;
    private String msg;
	public ErrorResponseEntity(int i, String message2) {
		this.code=i;
		this.msg=message2;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String message) {
		this.msg = message;
	}
}