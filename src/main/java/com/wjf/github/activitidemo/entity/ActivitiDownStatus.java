package com.wjf.github.activitidemo.entity;

/**
 * activiti工作流正向流转状态
 *
 * @author wjf
 */
public enum ActivitiDownStatus {

	LOWER_HAIR(0, "下发"),
	SUBMIT(1, "提交"),
	EXAMINE(2, "审核"),
	FILE(3, "归档");

	public static ActivitiDownStatus getActivitiDowenStatus(Integer integer) {
		if (integer == 0) {
			return LOWER_HAIR;
		}
		if (integer == 1) {
			return SUBMIT;
		}
		if (integer == 2) {
			return EXAMINE;
		}
		if (integer == 3) {
			return FILE;
		}
		return null;
	}

	private final Integer statusNumber;

	private final String statusName;

	ActivitiDownStatus(Integer statusNumber, String statusName) {
		this.statusName = statusName;
		this.statusNumber = statusNumber;
	}

	public Integer getStatusNumber() {
		return statusNumber;
	}

	public String getStatusName() {
		return statusName;
	}
}
