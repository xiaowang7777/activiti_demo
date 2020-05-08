package com.wjf.github.activitidemo.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 继承了 #{@link java.util.HashMap} 类，对该类进行了一些自定义功能的增强
 * @param <K>
 * @param <V>
 * @author wjf
 */
public class MyMap<K,V> extends HashMap<K,V> implements Map<K,V> {
	private static final long serialVersionUID = -2316454475269896231L;

	public V eject(K k){
		V v = super.get(k);
		super.remove(k);
		return v;
	}
}
