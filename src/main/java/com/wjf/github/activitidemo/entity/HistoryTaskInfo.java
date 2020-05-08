package com.wjf.github.activitidemo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
@ToString
public class HistoryTaskInfo implements Serializable {
	private static final long serialVersionUID = -6861009468447216411L;

	private String taskId;

	private String processDefinitionId;

	private String processInstanceId;

	private String taskName;

	private String taskDefinitionKey;

	private List<String> taskDefinitionKeys;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endTime;

	private String taskInfo;

	private String businessInfo;

	private String assignee;
}
