package com.wjf.github.activitidemo.util;

import java.util.ArrayList;

public class MyList<T> extends ArrayList<T> {

	private static final long serialVersionUID = -631929692936183594L;

	public T reject(int index){
		T t = get(index);
		remove(index);
		return t;
	}

}
