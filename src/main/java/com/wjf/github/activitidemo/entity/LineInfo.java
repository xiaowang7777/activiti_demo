package com.wjf.github.activitidemo.entity;

import lombok.*;

import java.io.Serializable;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LineInfo extends NodeBaseInfo implements Serializable {
	private static final long serialVersionUID = 3721747483897420982L;

	private String from;

	private String to;

	private String label;

}
