package com.wjf.github.activitidemo.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class MapUtil {

	public static <T> T MapToBean(Map<String, Object> map, Class<T> clazz) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
		T t = clazz.newInstance();

		Field[] fields = clazz.getDeclaredFields();

		String fieldName;
		String setMethodName;
		for (Field field : fields) {
			fieldName = field.getName();
			Object o = map.get(fieldName);
			if (o == null) {
				continue;
			}
			String start = fieldName.substring(0, 1);
			String end = fieldName.substring(1);
			setMethodName = "set" + start.toUpperCase() + end;
			Method method = clazz.getMethod(setMethodName, field.getType());
			if (method != null) {
				method.invoke(t, o);
			}
		}
		return t;
	}

}
