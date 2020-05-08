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
public class StartProcessReturnInfo<T> implements Serializable {
	private static final long serialVersionUID = 6693582369525945908L;

	private T data;

	private String processInstanceId;

}
