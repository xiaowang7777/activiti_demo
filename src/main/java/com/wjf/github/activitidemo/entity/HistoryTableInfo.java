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
public class HistoryTableInfo implements Serializable {
	private static final long serialVersionUID = 622017904383888463L;

	private String historyData;

	private Integer pk;

	private String businessTableName;

}
