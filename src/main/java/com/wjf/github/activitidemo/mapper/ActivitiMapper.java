package com.wjf.github.activitidemo.mapper;

import com.wjf.github.activitidemo.entity.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ActivitiMapper {

	String findPkNameByTable(String tableName);

	BusinessTableInfo findBusinessTableInfoByProcessInstanceId(String processInstanceId);

	Integer createNewProcessInstanceBusinessInfo(String processInstanceId, Integer tableId, Integer pk);

	Map<String, Object> executeSelectSql(String sql);

	Integer executeUpdateSql(String sql);

	Integer createNewHistoryDate(String historyData, String processInstanceId, String taskId);

	HistoryTableInfo findBusinessHistoryData(String processInstanceId,String taskId);

	List<TaskInfo> findHistoryActivitiInfo(String processInstanceId);

	HistoryTaskInfo findHistoryTaskInfo(String taskId);

	List<ActivitiTaskVariablesInfo> findHistoryTaskAndVariablesAndProcessInstance(String assignee);

	List<ActivitiTaskVariablesInfo> findMySelfTaskAndVariables(String assignee);

	List<ActivitiTaskVariablesInfo> findMySelfUserTaskBusinessByCandidate(String userId);

	List<ProcessInfo> findProcessDefinitionList();

	List<Integer> findUserListByGroup(String groupName);

	List<Integer> findUserListByGroups(String groupNames);

	List<ProcessDefinitionInfo> findAllProcessDefinitionInfo();

	TaskNormalInfo findUserTaskDetailInfo(String taskId);
}
