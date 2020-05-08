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
public class ActivitiTaskVariablesInfo implements Serializable {
	private static final long serialVersionUID = -4799253710101612409L;

	private String taskId;

	private String processInstanceId;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endTime;

	private String processDefinetionId;

	private String taskName;

	private String businessInfo;

	private String kind;

	private String name;

}
