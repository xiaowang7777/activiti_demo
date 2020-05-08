package com.wjf.github.activitidemo.service.impl;

/**
 * 解析排他网关的条件字符串
 */
public enum TrimUtil {

	EQUAL {
		@Override
		public Boolean getRes(Double target, String targetString) throws NumberFormatException {
			return target == Double.parseDouble(getFields(targetString)[1]);
		}

		@Override
		public String[] getFields(String target) {
			String all = target.replaceAll("$", "").replaceAll("\\{", "").replaceAll("}", "");

			return all.split("==");
		}
	},
	GREATER {
		@Override
		public Boolean getRes(Double target, String targetString) throws NumberFormatException {
			return target > Double.parseDouble(getFields(targetString)[1]);
		}

		@Override
		public String[] getFields(String target) {
			String all = target.replaceAll("$", "").replaceAll("\\{", "").replaceAll("}", "");
			return all.split(">");
		}
	},
	LESS {
		@Override
		public Boolean getRes(Double target, String targetString) throws NumberFormatException {
			return target < Double.parseDouble(getFields(targetString)[1]);
		}

		@Override
		public String[] getFields(String target) {
			String all = target.replaceAll("$", "").replaceAll("\\{", "").replaceAll("}", "");
			return all.split("<");
		}
	},
	GREATER_AND_EQUAL {
		@Override
		public Boolean getRes(Double target, String targetString) throws NumberFormatException {
			return target >= Double.parseDouble(getFields(targetString)[1]);
		}

		@Override
		public String[] getFields(String target) {
			String all = target.replaceAll("$", "").replaceAll("\\{", "").replaceAll("}", "");
			return all.split(">=");
		}
	},
	LESS_AND_EQUAL {
		@Override
		public Boolean getRes(Double target, String targetString) throws NumberFormatException {
			return target <= Double.parseDouble(getFields(targetString)[1]);
		}

		@Override
		public String[] getFields(String target) {
			String all = target.replaceAll("$", "").replaceAll("\\{", "").replaceAll("}", "");
			return all.split("<=");
		}
	};

	public static TrimUtil getTrimUtil(String s) {
		if (s.indexOf("==") > 0) {
			return EQUAL;
		}
		if (s.indexOf(">=") > 0) {
			return GREATER_AND_EQUAL;
		}
		if (s.indexOf("<=") > 0) {
			return LESS_AND_EQUAL;
		}
		if (s.indexOf(">") > 0) {
			return GREATER;
		}
		if (s.indexOf("<") > 0) {
			return LESS;
		}
		return null;
	}

	public abstract Boolean getRes(Double target, String targetString) throws NumberFormatException;

	public abstract String[] getFields(String target);
}
