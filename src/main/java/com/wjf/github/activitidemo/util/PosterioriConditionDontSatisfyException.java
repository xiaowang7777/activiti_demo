package com.wjf.github.activitidemo.util;

/**
 * 后验条件不满足时抛出的异常
 */
public class PosterioriConditionDontSatisfyException extends Throwable {
	private static final long serialVersionUID = -2282161405272625236L;

	public PosterioriConditionDontSatisfyException(){
		super("后验条件不满足！");
	}

}
