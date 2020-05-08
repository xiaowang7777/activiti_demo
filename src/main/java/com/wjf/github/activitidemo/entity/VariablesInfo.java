package com.wjf.github.activitidemo.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@Getter
@Setter
@ToString
public class VariablesInfo<T> implements Serializable {
	private static final long serialVersionUID = 6454576749581990113L;

	private String taskId;

	private T dataObject;

	private Date createTime;

}
