package com.wjf.github.activitidemo.util;

public class StartEventDontHaveAnyOutGoingFlowsException extends Throwable {
	private static final long serialVersionUID = 1918326751989481851L;

	public StartEventDontHaveAnyOutGoingFlowsException(){
		super("该起始事件未有任何外连线!");
	}

}
