package com.wjf.github.activitidemo.util;

public class UserTaskCantRollBackException extends Throwable {
	private static final long serialVersionUID = -656939844358967295L;
	public UserTaskCantRollBackException(){
		super("用户任务不可回滚！");
	}
}
