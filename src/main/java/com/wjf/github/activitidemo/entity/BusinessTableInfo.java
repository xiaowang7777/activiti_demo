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
public class BusinessTableInfo implements Serializable {
	private static final long serialVersionUID = -5270003362719411256L;

	private String businessTableName;

	private Integer pk;

}
