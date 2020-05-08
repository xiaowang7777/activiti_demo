package com.wjf.github.activitidemo.util;


public class UserTaskDontMatchException extends Throwable {
	private static final long serialVersionUID = -8811161014987958737L;

	public UserTaskDontMatchException(){
		super("用户任务执行人不匹配！");
	}

	public UserTaskDontMatchException(String s){
		super(s);
	}

}
