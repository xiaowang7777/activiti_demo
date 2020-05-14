package com.wjf.github.activitidemo.util;

import com.wjf.github.activitidemo.entity.LineInfo;
import com.wjf.github.activitidemo.entity.NodeBaseInfo;
import com.wjf.github.activitidemo.entity.NodeInfo;
import org.activiti.bpmn.model.*;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.Collections;

public enum FlowElementGenerateUtil {

	START_EVENT() {
		@Override
		public FlowElement createFlowElement(String id, String source, String target, String condition) {
			return null;
		}

		public FlowElement createFlowElement(String name, String id) {
			return super.getStartEvent(name, id);
		}
	},
	END_EVENT() {
		@Override
		public FlowElement createFlowElement(String id, String source, String target, String condition) {
			return null;
		}

		@Override
		public FlowElement createFlowElement(String name, String id) {
			return super.getEndEvent(name, id);
		}
	},
	USER_TASK() {
		@Override
		public FlowElement createFlowElement(String id, String fromKey, String candidateUser, String name) {
			return super.getUserTask(id, fromKey, candidateUser, name);
		}

		@Override
		public FlowElement createFlowElement(String name, String id) {
			return null;
		}
	},
	SEQUENCE_FLOW() {
		@Override
		public FlowElement createFlowElement(String id, String source, String target, String condition) {
			return super.getSequenceFlow(id, source, target, condition);
		}

		@Override
		public FlowElement createFlowElement(String name, String id) {
			return null;
		}
	};

	public static FlowElement createFlowElement(NodeBaseInfo nodeBaseInfo) {
		if (nodeBaseInfo instanceof NodeInfo) {
			NodeInfo nodeInfo = (NodeInfo) nodeBaseInfo;
			if (nodeInfo.getType() == 1) {
				return START_EVENT.createFlowElement(nodeInfo.getName(), "_" + nodeInfo.getId());
			} else if (nodeInfo.getType() == 2) {
				return END_EVENT.createFlowElement(nodeInfo.getName(), "_" + nodeInfo.getId());
			}
			return USER_TASK.createFlowElement("_" + nodeInfo.getId(), nodeInfo.getPath() + "", nodeInfo.getUserGroup(), nodeInfo.getName());
		} else {
			LineInfo lineInfo = (LineInfo) nodeBaseInfo;
			return SEQUENCE_FLOW.createFlowElement("_" + lineInfo.getId(), "_" + lineInfo.getFrom(), "_" + lineInfo.getTo(), null);
		}
	}

	/**
	 * 生成开始节点
	 *
	 * @param name
	 * @param id
	 * @return
	 */
	private StartEvent getStartEvent(String name, @NotNull String id) {
		StartEvent startEvent = new StartEvent();
		startEvent.setName(name);
//		流程编号不能以数字开头
		startEvent.setId("_" + id);
		return startEvent;
	}

	/**
	 * 生成用户任务节点
	 *
	 * @param id
	 * @param fromKey
	 * @param candidateUser
	 * @return
	 */
	private UserTask getUserTask(@NotNull String id, String fromKey, String candidateUser, String name) {
		UserTask userTask = new UserTask();
		userTask.setId("_" + id);
		userTask.setFormKey(fromKey);
		userTask.setCandidateUsers(Collections.singletonList("#{" + candidateUser.replaceAll(" ", "") + "}"));
		userTask.setName(name);
		return userTask;
	}

	/**
	 * 生成连线
	 *
	 * @param source
	 * @param target
	 * @param condition
	 * @return
	 */
	private SequenceFlow getSequenceFlow(@NotNull String id, String source, String target, String condition) {
		SequenceFlow sequenceFlow = new SequenceFlow();
		sequenceFlow.setId("_" + id);
		sequenceFlow.setSourceRef("_" + source);
		sequenceFlow.setTargetRef("_" + target);
		if (StringUtils.isNotEmpty(condition)) {
			sequenceFlow.setConditionExpression(condition);
		}
		return sequenceFlow;
	}

	/**
	 * 生成结束节点
	 *
	 * @param name
	 * @param id
	 * @return
	 */
	private EndEvent getEndEvent(String name, @NotNull String id) {
		EndEvent endEvent = new EndEvent();
		endEvent.setName(name);
		endEvent.setId("_" + id);
		return endEvent;
	}

	public abstract FlowElement createFlowElement(String id, String source, String target, String condition);

	public abstract FlowElement createFlowElement(String name, String id);
}
