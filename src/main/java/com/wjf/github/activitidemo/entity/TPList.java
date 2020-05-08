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
public class TPList implements Serializable {

	private static final long serialVersionUID = -3945402382535995791L;

	private Integer tpListId;

	private String tpReDeployId;

	private String tpReProcdefId;

}
