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
public class BusinessInfo implements Serializable {
	private static final long serialVersionUID = 4422453048603397101L;

	private Integer id;

	private String name;

	private String someInfo;

}
