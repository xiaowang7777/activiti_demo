package com.wjf.github.activitidemo.entity;

import lombok.*;

import java.io.Serializable;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FlowInfo implements Serializable {
	private static final long serialVersionUID = -7733597657095709451L;

	private String id;

	private String name;

	private Double width;

	private Double height;

}
