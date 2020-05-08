package com.wjf.github.activitidemo.util;

import java.util.Map;

/**
 * 工作流所需
 * 业务数据校验
 * 用于用户任务开始前的业务数据的前验条件和用户任务完成后的业务数据的后验条件
 * 项目后期扩展预留
 * @author wjf
 */
@FunctionalInterface
public interface BusinessDataVerification {

	Boolean execute(Map<String, Object> values);

}
