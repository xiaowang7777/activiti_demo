package com.wjf.github.activitidemo.entity;

import lombok.*;

import java.io.Serializable;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProcessDefinitionInfo implements Serializable {
	private static final long serialVersionUID = 3916267233167197427L;

	private String id;

	private String name;
}
