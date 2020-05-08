package com.wjf.github.activitidemo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@Setter
@Getter
@ToString
public class BusinessData implements Serializable {
	private static final long serialVersionUID = 1425620762704356644L;

	private String taskId;

	private String dataJson;

	private Integer businessKind;

	private String processInstanceId;

	private String name;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date date;
}
