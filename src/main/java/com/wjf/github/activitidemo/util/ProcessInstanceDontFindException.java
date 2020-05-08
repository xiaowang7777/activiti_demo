package com.wjf.github.activitidemo.util;


public class ProcessInstanceDontFindException extends Throwable {
	private static final long serialVersionUID = 6601942442673240104L;

	public ProcessInstanceDontFindException(){
		super("流程实例未找到！");
	}

}
