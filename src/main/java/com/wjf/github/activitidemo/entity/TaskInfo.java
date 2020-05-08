package com.wjf.github.activitidemo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@Getter
@Setter
@ToString
public class TaskInfo implements Serializable {
	private static final long serialVersionUID = 2050532939032825764L;

	private String taskId;

	private String taskName;

	private String processInstanceId;

	private String processDefinitionId;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endTime;

	private String taskInfo;

}
