package com.wjf.github.activitidemo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class MapUtil {

	private final static Logger logger = LoggerFactory.getLogger(MapUtil.class);

	public static <T> T MapToBean(Map<String, Object> map, Class<T> clazz) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException {
		//new一个指定类型的对象
		T t = clazz.newInstance();

		StringBuilder setMethodName;
		StringBuilder fieldName;

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			setMethodName = new StringBuilder("set");
			fieldName = new StringBuilder();

			String key = entry.getKey();
			Object o = entry.getValue();

			String[] split = key.split("_");

			for (String s1 : split) {
				setMethodName.append(s1.substring(0, 1).toUpperCase()).append(s1.substring(1));
				fieldName.append(s1.substring(0, 1).toUpperCase()).append(s1.substring(1));
			}
			String s = fieldName.toString().substring(0, 1).toLowerCase();

			Field declaredField;

			try {
				declaredField = clazz.getDeclaredField(s + fieldName.toString().substring(1));
			} catch (NoSuchFieldException e) {
				continue;
			}

			if (declaredField == null) {
				continue;
			}

			Method method = clazz.getMethod(setMethodName.toString(), declaredField.getType());
			if (method != null) {
				method.invoke(t, o);
			}
		}
		return t;
	}

}
