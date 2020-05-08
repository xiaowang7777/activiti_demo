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
public class ProcessInfo implements Serializable {
	private static final long serialVersionUID = -4249020026782598708L;

	private String processName;

	private String processDefinitionId;

	private String deployId;

}
