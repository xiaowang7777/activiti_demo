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
public class ActivitiStatus implements Serializable {
	private static final long serialVersionUID = -6153076669596347513L;

	private Integer jurisdictionId;

	private ActivitiUpStatus upStatus;

	private ActivitiDownStatus downStatus;

	private Boolean upload;

}
