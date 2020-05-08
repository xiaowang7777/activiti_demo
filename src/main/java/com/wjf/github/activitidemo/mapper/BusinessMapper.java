package com.wjf.github.activitidemo.mapper;

import com.wjf.github.activitidemo.entity.BusinessInfo;
import com.wjf.github.activitidemo.entity.GroupInfo;
import com.wjf.github.activitidemo.entity.InterfaceInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessMapper {

	Integer activitiOperation(String someInfo,Integer Id);

	Integer activitiOperation1(String name,Integer Id);

	Integer activitiOperation2(String someInfo,String name,Integer Id);

	Integer createNewBusinessInfo(BusinessInfo businessInfo);

	List<InterfaceInfo> findBusinessPathInfo();

	List<GroupInfo> findAllGroupInfo();

	List<BusinessInfo> findAllBusinessInfo();
}
