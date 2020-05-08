package com.wjf.github.activitidemo.controller;

import com.wjf.github.activitidemo.util.*;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@ResponseBody
@ControllerAdvice(value = {"com.wjf.github.activitidemo.controller"})
public class OverallSituationExceptionHandleController {

	@ExceptionHandler(value = UserTaskDontFindException.class)
	public ResponseMap userTaskDontFindExceptionHandler(){
		return ResponseMap.getFailResult("该用户任务未找到！");
	}

	@ExceptionHandler(value = UserTaskCantRollBackException.class)
	public ResponseMap userTaskCantRollBackExceptionHandler(){
		return ResponseMap.getFailResult("该用户任务不能回滚！");
	}

	@ExceptionHandler(value = UserTaskDontMatchException.class)
	public ResponseMap userTaskDontMatchExceptionHandler(){
		return ResponseMap.getFailResult("用户任务不匹配！");
	}

	@ExceptionHandler(value = IOException.class)
	public ResponseMap iOExceptionHandler(IOException e){
		e.printStackTrace();
		return ResponseMap.getFailResult("IO流操作异常！");
	}

	@ExceptionHandler(value = ProcessInstanceDontFindException.class)
	public ResponseMap processInstanceDontFindExceptionHandler(){
		return ResponseMap.getFailResult("流程实例未找到！");
	}

	@ExceptionHandler(value = StartEventDontHaveAnyOutGoingFlowsException.class)
	public ResponseMap startEventDontHaveAnyOutGoingFlowsExceptionHandler(){
		return ResponseMap.getFailResult("该开始节点没有任何外连线！");
	}

	@ExceptionHandler(value = KindCantUseException.class)
	public ResponseMap kindCantUseExceptionHandler(){
		return ResponseMap.getFailResult("业务种类不可用！");
	}

	@ExceptionHandler(value = PosterioriConditionDontSatisfyException.class)
	public ResponseMap posterioriConditionDontSatisfyExceptionHandler(){
		return ResponseMap.getFailResult("业务后置验证条件不满足！未进行任何更改！");
	}

	@ExceptionHandler(value = PriorConditionDontSatisfyException.class)
	public ResponseMap PriorConditionDontSatisfyExceptionHandler(){
		return ResponseMap.getFailResult("业务前置验证条件不满足！未进行任何更改！");
	}
}
