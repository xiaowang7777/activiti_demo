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
public class ActivitiStatusVo implements Serializable {
	private static final long serialVersionUID = -5630059428422545456L;

	private Integer jurisdictionId;

	private Integer upStatus;

	private Integer downStatus;

	private Boolean upload;

}
