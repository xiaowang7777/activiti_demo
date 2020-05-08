package com.wjf.github.activitidemo.util;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

//自定义的分页信息类，主要用于实现无法使用分页插件的SQL语句进行分页
@Data
@Getter
@Setter
@ToString
public class MyPage<T> implements Serializable {
	private static final long serialVersionUID = 3498903500049781707L;

	private Integer pageNum;

	private Integer pageSize;

	private Integer pageCount;

	private Integer dataCount;

	private List<T> data;

}
