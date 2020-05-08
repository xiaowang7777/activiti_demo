package com.wjf.github.activitidemo.util;

public class UserTaskDontFindException extends Throwable {
	private static final long serialVersionUID = 3540091570155679549L;

	public UserTaskDontFindException(){
		super("该用户任务已完成，或不存在！");
	}

	public UserTaskDontFindException(String s){
		super(s);
	}

}
