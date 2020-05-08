package com.wjf.github.activitidemo.util;

public class KindCantUseException extends Throwable {
	private static final long serialVersionUID = -351379537136856919L;

	public KindCantUseException(){
		super("该类型暂不支持！");
	}

}
