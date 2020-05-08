package com.wjf.github.activitidemo.util;

import java.io.IOException;
import java.util.Map;

/**
 * 工作流所需的业务处理接口
 * @author wjf
 */
@FunctionalInterface
public interface GetObjectByValue<T> {

	T execute(Map<String, Object> business) throws ProcessInstanceDontFindException, IOException;

}
