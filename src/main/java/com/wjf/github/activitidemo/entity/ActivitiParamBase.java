package com.wjf.github.activitidemo.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Data
@Getter
@Setter
@ToString
public abstract class ActivitiParamBase implements Serializable {
	private static final long serialVersionUID = -4247791044800041184L;

	private String processInstanceId;

	private String taskId;

}
