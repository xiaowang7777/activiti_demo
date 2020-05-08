package com.wjf.github.activitidemo.entity;

import lombok.*;

import java.io.Serializable;

@Data
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GroupInfo implements Serializable {
	private static final long serialVersionUID = 6321081600719974883L;

	private Integer groupId;

	private String groupName;

}
