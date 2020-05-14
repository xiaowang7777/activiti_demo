package com.wjf.github.activitidemo.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TaskInterface implements Serializable {
	private static final long serialVersionUID = 6435062015022452916L;

	private String taskId;

	private String processInstanceId;

	private String processDefinitionId;

	private Date createTime;

	private String interfacePath;

}
