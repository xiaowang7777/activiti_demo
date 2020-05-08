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
public class ActivitiTpList implements Serializable {
	private static final long serialVersionUID = 3107305492355451886L;

	private Integer tpId;

	private String listName;

	private String procdefId;

	private String deployId;

}
