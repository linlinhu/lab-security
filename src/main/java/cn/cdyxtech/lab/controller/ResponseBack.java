/**
 * 
 */
package cn.cdyxtech.lab.controller;

import com.emin.base.exception.EminException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author jim.lee
 *
 */
@Component
public class ResponseBack<T> implements Serializable{


	@Value("${defaultErrorMsg}")
	private static String DEFAULT_ERROR_MSG;
	/**
	 * 
	 */
	private static final long serialVersionUID = -5700125269437239109L;
	
	private Boolean success;     
	private String message;
	private T result;

	public ResponseBack() {
	}

	/* 成功处理 */
	public static final <T> ResponseBack<T> success(T t) {
		return new ResponseBack<>("ok",true, t);
	}


	private ResponseBack(String message,boolean success, T result) {
		this.message = message;
		this.result = result;
		this.success = success;
	}


	public ResponseBack(Exception e) {
		this.message = e instanceof EminException ? e.getLocalizedMessage():DEFAULT_ERROR_MSG;
		this.result = null;
		this.success = false;
	}

	/* Exception处理 */
	public static <T> ResponseBack<T> error(Exception e) {
		return new ResponseBack<>(e);
	}

	public static <T> ResponseBack<T> error(String message) {
		return new ResponseBack(message,false, null);
	}


	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}


}
