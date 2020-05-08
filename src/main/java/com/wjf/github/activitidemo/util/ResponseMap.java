package com.wjf.github.activitidemo.util;


import java.io.Serializable;

public class ResponseMap<T> implements Serializable {

	private static final long serialVersionUID = 7907006739480161575L;

	private final Integer code;

	private final String sym;

	private final String msg;

	private final T data;

	private ResponseMap(Integer code, String sym, String msg, T data) {
		this.code = code;
		this.sym = sym;
		this.msg = msg;
		this.data = data;
	}

	public Integer getCode() {
		return code;
	}

	public String getSym() {
		return sym;
	}

	public String getMsg() {
		return msg;
	}

	public T getData() {
		return data;
	}

	public static <T> ResponseMap<T> getSuccessResult() {
		return new ResponseMap<>(200,"ok","请求成功！",null);
	}

	public static <T> ResponseMap<T> getSuccessResult(String msg) {
		return new ResponseMap<>(200,"ok",msg,null);
	}

	public static <T> ResponseMap<T> getSuccessResult(T data) {
		return new ResponseMap<>(200,"ok","请求成功！",data);
	}

	public static <T> ResponseMap<T> getSuccessResult(String msg, T data) {
		return new ResponseMap<>(200,"ok",msg,data);
	}

	public static <T> ResponseMap<T> getFailResult() {
		return new ResponseMap<>(500,"fail","请求失败！",null);
	}

	public static <T> ResponseMap<T> getFailResult(Integer code) {
		return new ResponseMap<>(code,"fail","请求失败！",null);
	}

	public static <T> ResponseMap<T> getFailResult(String msg) {
		return new ResponseMap<>(500,"fail",msg,null);
	}

	public static <T> ResponseMap<T> getFailResult(T data) {
		return new ResponseMap<>(500,"fail","请求失败！",data);
	}

	public static <T> ResponseMap<T> getFailResult(Integer code, T data) {
		return new ResponseMap<>(code,"fail","请求失败！",data);
	}

	public static <T> ResponseMap<T> getFailResult(Integer code, String msg) {
		return new ResponseMap<>(code,"fail",msg,null);
	}

	public static <T> ResponseMap<T> getFailResult(String msg, T data) {
		return new ResponseMap<>(500,"fail",msg,data);
	}

	public static <T> ResponseMap<T> getFailResult(Integer code, String msg, T data) {
		return new ResponseMap<>(code,"fail",msg,data);
	}
}
