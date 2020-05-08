package com.wjf.github.activitidemo.util;

/**
 * 前验条件不满足时抛出的异常
 */
public class PriorConditionDontSatisfyException extends Throwable {
	private static final long serialVersionUID = 2605731700850380637L;

	public PriorConditionDontSatisfyException(){
		super("前验条件不满足！");
	}

}
