package com.wjf.github.activitidemo.service;

import com.wjf.github.activitidemo.entity.*;
import com.wjf.github.activitidemo.util.*;
import org.activiti.bpmn.model.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.apache.batik.transcoder.TranscoderException;
import org.dom4j.DocumentException;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface ActivitiService {

	@Transactional
	Boolean deployNewActiviti(String zipPath, String name, String fileName) throws FileNotFoundException;

	Boolean deployNewActiviti(BpmnModel bpmnModel, String deployName);

	Boolean deployNewActiviti(InputStream inputStream, String deployName);

	@Transactional
	<T> StartProcessReturnInfo<T> startTechnologicalProcess(String processDefinitionId, Map<String, Object> processValue,
	                                                        Map<String, Object> businessValue, List<Integer> businessId, Integer operationTableId,
	                                                        GetObjectByValue<T> businessMethod, Map<String, Object> priorValues, BusinessDataVerification priorVerification,
	                                                        Map<String, Object> posteriorValues, BusinessDataVerification posterioriVerification) throws PriorConditionDontSatisfyException, PosterioriConditionDontSatisfyException, ProcessInstanceDontFindException, IOException, KindCantUseException, StartEventDontHaveAnyOutGoingFlowsException, UserTaskDontFindException;

	@Transactional
	<T> T completeUserTask(String taskId, Integer operationUserIdNow, Map<String, Object> processValue,
	                       Map<String, Object> businessValue, GetObjectByValue<T> businessMethod,
	                       Map<String, Object> priorValues, BusinessDataVerification priorVerification,
	                       Map<String, Object> posteriorValues, BusinessDataVerification posterioriVerification, Class clazz) throws UserTaskDontMatchException, PriorConditionDontSatisfyException, PosterioriConditionDontSatisfyException, IOException, ProcessInstanceDontFindException, NumberFormatException, UserTaskDontFindException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException;

	<T> Map<String, Object> getVariablesByBusiness(String taskId, T t);

	@Transactional
	void rollBackUserTask(String processInstanceId, String taskId, HistoricTaskInstance historyTask, Map<String, Object> processValue) throws UserTaskDontFindException, UserTaskDontMatchException, IOException;

	List<HistoricTaskInstance> findAllHistoryUserTask(String processInstanceId);

	HistoricTaskInstance getLastUserTaskByProcessInstanceId(String processInstanceId);

	HistoricTaskInstance getLastUserTaskByProcessInstanceIdAndTaskId(String processInstanceId, String taskId) throws UserTaskCantRollBackException, UserTaskDontFindException, ProcessInstanceDontFindException;

	void setVariablesByProcessInstance(String processInstanceId, Map<String, Object> variables) throws ProcessInstanceDontFindException;

	Map<String, Object> getVariablesByProcessInstance(String processInstanceId) throws ProcessInstanceDontFindException;

	void getActivityResourceDiagramInputStream(String processInstanceId, OutputStream outputStream) throws TranscoderException, DocumentException, IOException, ProcessInstanceDontFindException;

	void getActivityResourceDiagramInputStreamByProcessDefinitionId(String processDefinitionId, OutputStream outputStream) throws DocumentException, IOException, TranscoderException;

	InputStream getStaticResourceDiagramInputStream(String processInstanceId);

	//	获取用户的个人任务
	MyPage<Task> findMySelfUserTask(Integer userId, Integer pageNum, Integer pageSize);

	List<ActivitiTaskVariablesInfo> findMySelfUserTaskBusiness(Integer userId, Integer pageNum, Integer pageSize);

	MyPage<Task> findMySelfUserTaskByCandidate(String group, Integer pageNum, Integer pageSize);

	List<ActivitiTaskVariablesInfo> findMySelfUserTaskBusinessByCandidate(String userId);

	Task getMyTaskByProcessInstanceId(@NotNull String processInstanceId, @NotNull Integer userId);

	Boolean judgeProcessInstanceIsCompleteByTaskId(@NotNull String taskId) throws ProcessInstanceDontFindException;

	List<ActivitiTaskVariablesInfo> getHistoryTaskByAssignee(String assignee, Integer pageNum, Integer pageSize);

	List<ProcessInfo> findProcessDefinitionList();

	NowTask findNowTaskInfo(String taskId) throws UserTaskDontFindException;

	String findHistoryBusinessData(String processInstanceId, String variableName);

	String findProcessInstanceIdByTaskId(String taskId) throws UserTaskDontFindException;

	String findProcessDefinitionIdByTaskId(String taskId) throws UserTaskDontFindException;

	HistoryTaskInfo findHistoryTaskInfo(String taskId);

	List<TaskInfo> findHistoryActivitiInfo(String processInstanceId);

	void userTaskClaim(String taskId, String userId);

	void userTaskResetAssignee(String taskId);

	StartEvent getStartEvent(String name, @NotNull String id);

	UserTask getUserTask(@NotNull String id, String fromKey, String candidateUser);

	SequenceFlow getSequenceFlow(@NotNull String id, String source, String target, String condition);

	EndEvent getEndEvent(String name, @NotNull String id);

	List<ProcessDefinitionInfo> findAllProcessDefinitionInfo();

	TaskNormalInfo findUserTaskDetailInfo(String taskId);

	TaskInterface findNowTaskInfo(String taskId, String assignee);

	List<ActivitiTaskVariablesInfo> findMySelfUserTaskByAssignee(String userId);
}
