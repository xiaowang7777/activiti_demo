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
public class SaveConfigInfo implements Serializable {
	private static final long serialVersionUID = -2684144898316625692L;

	private Integer businessId;

	private String tableName;

	private String pkName;

}
