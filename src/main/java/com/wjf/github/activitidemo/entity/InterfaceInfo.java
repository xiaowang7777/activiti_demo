package com.wjf.github.activitidemo.entity;

import lombok.*;

import java.io.Serializable;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InterfaceInfo implements Serializable {
	private static final long serialVersionUID = 7323308596972816830L;

	private Integer id;

	private String interfaceName;

	private String interfacePath;

}
