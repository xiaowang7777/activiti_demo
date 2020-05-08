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
public abstract class NodeBaseInfo implements Serializable {

	private static final long serialVersionUID = 4031434329210295355L;
	private String id;

	private String name;

	private Double width;

	private Double height;

	private Double x;

	private Double y;

}
