package com.wjf.github.activitidemo.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.activiti.bpmn.model.UserTask;

import java.io.Serializable;
import java.util.List;

@Data
@Getter
@Setter
@ToString
public class NowTask implements Serializable {
	private static final long serialVersionUID = 7556342738075867578L;

	private String nowTaskKey;

	private String nowTaskId;

	private List<UserTask> userTasks;

	private ActivitiStatusVo jurisdiction;

	private Integer businessId;

}
