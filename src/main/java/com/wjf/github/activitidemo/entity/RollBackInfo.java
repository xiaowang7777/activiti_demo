package com.wjf.github.activitidemo.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Data
@Setter
@Getter
@ToString
public class RollBackInfo implements Serializable {
	private static final long serialVersionUID = 4459611251465783398L;

	private String historyData;

	private String userTaskId;

	private String tableName;

	private String pkName;

	private Integer businessId;

}
