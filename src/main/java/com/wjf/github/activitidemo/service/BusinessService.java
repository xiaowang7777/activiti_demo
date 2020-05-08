package com.wjf.github.activitidemo.service;

import com.wjf.github.activitidemo.entity.BusinessInfo;
import com.wjf.github.activitidemo.entity.GroupInfo;
import com.wjf.github.activitidemo.entity.InterfaceInfo;

import java.util.List;
import java.util.Map;

public interface BusinessService {

	Integer activitiOperation(String someInfo,Integer Id);

	Integer activitiOperation1(String name,Integer Id);

	Integer activitiOperation2(String someInfo,String name,Integer Id);

	Integer createNewBusinessInfo(BusinessInfo businessInfo);

	List<InterfaceInfo> findBusinessPathInfo();

	List<GroupInfo> findAllGroupInfo();

	List<BusinessInfo> findAllBusinessInfo();
}
