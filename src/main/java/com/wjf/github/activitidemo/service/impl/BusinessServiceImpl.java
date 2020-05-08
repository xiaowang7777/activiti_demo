package com.wjf.github.activitidemo.service.impl;

import com.wjf.github.activitidemo.entity.BusinessInfo;
import com.wjf.github.activitidemo.entity.GroupInfo;
import com.wjf.github.activitidemo.entity.InterfaceInfo;
import com.wjf.github.activitidemo.mapper.BusinessMapper;
import com.wjf.github.activitidemo.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BusinessServiceImpl implements BusinessService {

	@Autowired
	private BusinessMapper businessMapper;

	@Override
	public Integer activitiOperation(String someInfo, Integer Id) {
		return businessMapper.activitiOperation(someInfo, Id);
	}

	@Override
	public Integer activitiOperation1(String name, Integer Id) {
		return businessMapper.activitiOperation1(name, Id);
	}

	@Override
	public Integer activitiOperation2(String someInfo, String name, Integer Id) {
		return businessMapper.activitiOperation2(someInfo, name, Id);
	}

	@Override
	public Integer createNewBusinessInfo(BusinessInfo businessInfo){
		return businessMapper.createNewBusinessInfo(businessInfo);
	}

	@Override
	public List<InterfaceInfo> findBusinessPathInfo() {
		return businessMapper.findBusinessPathInfo();
	}

	@Override
	public List<GroupInfo> findAllGroupInfo() {
		return businessMapper.findAllGroupInfo();
	}

	@Override
	public List<BusinessInfo> findAllBusinessInfo() {
		return businessMapper.findAllBusinessInfo();
	}
}
