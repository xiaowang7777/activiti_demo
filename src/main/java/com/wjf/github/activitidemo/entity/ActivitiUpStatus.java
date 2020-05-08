package com.wjf.github.activitidemo.entity;

/**
 * activiti工作流反向流转时的状态
 *
 * @author wjf
 */
public enum ActivitiUpStatus {

	REJECT(0, "驳回"),

	REGRESSION(1, "回退");

	private final Integer statusNumber;

	private final String statusName;

	public static ActivitiUpStatus getActivitiUpStatus(Integer integer) {
		if (integer == 1) {
			return REGRESSION;
		}
		if (integer == 0) {
			return REJECT;
		}
		return null;
	}

	ActivitiUpStatus(Integer statusNumber, String statusName) {
		this.statusNumber = statusNumber;
		this.statusName = statusName;
	}

	public Integer getStatusNumber() {
		return statusNumber;
	}

	public String getStatusName() {
		return statusName;
	}
}
