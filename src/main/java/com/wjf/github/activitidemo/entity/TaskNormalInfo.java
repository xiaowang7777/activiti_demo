package com.wjf.github.activitidemo.entity;

import lombok.*;

import java.io.Serializable;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TaskNormalInfo implements Serializable {
	private static final long serialVersionUID = -6721608453313349854L;

	private String taskId;

	private String processInstanceId;

	private String processDefinitionId;

	private String info;

}
