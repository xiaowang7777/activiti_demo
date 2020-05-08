package com.wjf.github.activitidemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wjf.github.activitidemo.entity.*;
import com.wjf.github.activitidemo.service.ActivitiService;
import com.wjf.github.activitidemo.service.BusinessService;
import com.wjf.github.activitidemo.util.*;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Controller
@ResponseBody
@RequestMapping("/business")
public class BusinessController {

	@Autowired
	private BusinessService businessService;

	@Autowired
	private ActivitiService activitiService;

	@Autowired
	private ObjectMapper objectMapper;


	@PostMapping("/activitiOperation")
	public ResponseMap<String> activitiOperation(@RequestBody MyMap<String, Object> values) throws UserTaskDontFindException, IOException, ProcessInstanceDontFindException, PosterioriConditionDontSatisfyException, UserTaskDontMatchException, PriorConditionDontSatisfyException {

		String taskId = values.eject("taskId") + "";
		Integer userId = (Integer) values.eject("userId");

		BusinessInfo businessInfo = new BusinessInfo();
		if (values.get("id") != null && !"".equals(values.get("id"))) {
			businessInfo.setId((Integer) values.get("id"));
		}
		if (values.get("name") != null && !"".equals(values.get("name"))) {
			businessInfo.setName(values.get("name") + "");
		}
		if (values.get("someInfo") != null && !"".equals(values.get("someInfo"))) {
			businessInfo.setSomeInfo(values.get("someInfo") + "");
		}

		BusinessDataVerification preposition = map -> true;

		GetObjectByValue<Integer> business = map -> businessService.activitiOperation(map.get("someInfo") == null ? null
				: map.get("someInfo") + "", map.get("id") == null ? null : ((Integer) map.get("id")));

		BusinessDataVerification postposition = map -> true;

		Map<String, Object> variables = activitiService.getVariablesByBusiness(taskId, businessInfo);
		Integer completeUserTask = activitiService.completeUserTask(taskId, userId, variables, values, business, null, preposition, null, postposition);
		return completeUserTask > 0 ? ResponseMap.getSuccessResult() : ResponseMap.getFailResult();
	}

	@PostMapping("/activitiOperation1")
	public ResponseMap<String> activitiOperation1(@RequestBody MyMap<String, Object> values) throws UserTaskDontFindException, IOException, ProcessInstanceDontFindException, PosterioriConditionDontSatisfyException, UserTaskDontMatchException, PriorConditionDontSatisfyException {
		String taskId = values.eject("taskId") + "";
		Integer userId = (Integer) values.eject("userId");

		BusinessInfo businessInfo = new BusinessInfo();
		if (values.get("name") != null && !"".equals(values.get("name"))) {
			businessInfo.setName(values.get("name") + "");
		}
		if (values.get("id") != null && !"".equals(values.get("id"))) {
			businessInfo.setId((Integer) values.get("id"));
		}
		if (values.get("someInfo") != null && !"".equals(values.get("someInfo"))) {
			businessInfo.setSomeInfo(values.get("someInfo") + "");
		}
		BusinessDataVerification preposition = map -> true;
		BusinessDataVerification postposition = map -> true;
		GetObjectByValue<Integer> business = map -> businessService.activitiOperation1(map.get("name") == null ? null
				: map.get("name") + "", map.get("id") == null ? null : ((Integer) map.get("id")));
		Map<String, Object> variables = activitiService.getVariablesByBusiness(taskId, businessInfo);
		Integer completeUserTask = activitiService.completeUserTask(taskId, userId, variables, values, business, null, preposition, null, postposition);
		return completeUserTask > 0 ? ResponseMap.getSuccessResult() : ResponseMap.getFailResult();
	}

	@PostMapping("/activitiOperation2")
	public ResponseMap<String> activitiOperation2(@RequestBody MyMap<String, Object> values) throws UserTaskDontFindException, IOException, ProcessInstanceDontFindException, PosterioriConditionDontSatisfyException, UserTaskDontMatchException, PriorConditionDontSatisfyException {
		String taskId = values.eject("taskId") + "";
		Integer userId = (Integer) values.eject("userId");

		BusinessInfo businessInfo = new BusinessInfo();
		if (values.get("someInfo") != null && !"".equals(values.get("someInfo"))) {
			businessInfo.setSomeInfo(values.get("someInfo") + "");
		}
		if (values.get("name") != null && !"".equals(values.get("name"))) {
			businessInfo.setName(values.get("name") + "");
		}
		BusinessDataVerification preposition = map -> true;
		BusinessDataVerification postposition = map -> true;
		if (values.get("id") != null && !"".equals(values.get("id"))) {
			businessInfo.setId((Integer) values.get("id"));
		}
		GetObjectByValue<Integer> business = map -> businessService.activitiOperation2(map.get("someInfo") == null ? null
				: map.get("someInfo") + "", map.get("name") == null ? null
				: map.get("name") + "", map.get("id") == null ? null : ((Integer) map.get("id")));
		Map<String, Object> variables = activitiService.getVariablesByBusiness(taskId, businessInfo);
		Integer completeUserTask = activitiService.completeUserTask(taskId, userId, variables, values, business, null, preposition, null, postposition);
		return completeUserTask > 0 ? ResponseMap.getSuccessResult() : ResponseMap.getFailResult();
	}

	@PostMapping("/start")
	public ResponseMap<String> start(@RequestBody MyMap<String, Object> values) throws UserTaskDontFindException,
			StartEventDontHaveAnyOutGoingFlowsException, KindCantUseException, IOException,
			ProcessInstanceDontFindException, PosterioriConditionDontSatisfyException, PriorConditionDontSatisfyException {

		String processDefinitionId = values.eject("processDefinitionId") + "";
		BusinessDataVerification preposition = map -> true;
		final List<Integer> list = Collections.synchronizedList(new ArrayList<>(1));
		final BusinessInfo businessInfo = new BusinessInfo();
		businessInfo.setName(values.get("name") == null ? null : values.get("name") + "");
		businessInfo.setSomeInfo(values.get("someInfo") == null ? null : values.get("someInfo") + "");
		final Map<String, Object> data = new HashMap<>();
		GetObjectByValue<Integer> businessMethod = business -> {
			Integer info = businessService.createNewBusinessInfo(businessInfo);
			System.out.println("businessId--->" + businessInfo.getId());
			businessInfo.setId(businessInfo.getId());
			data.putAll(activitiService.getVariablesByBusiness("data", businessInfo));

			list.add(businessInfo.getId());
			return info;
		};


		BusinessDataVerification postposition = map -> true;
		Integer operationTableId = (Integer) values.eject("operationTableId");
		StartProcessReturnInfo<Integer> startProcessReturnInfo = activitiService.startTechnologicalProcess(processDefinitionId, data, values, list, operationTableId, businessMethod, null, preposition, null, postposition);

		return startProcessReturnInfo.getData() > 0 ? ResponseMap.getSuccessResult("请求成功！", startProcessReturnInfo.getProcessInstanceId())
				: ResponseMap.getFailResult("业务流程关系绑定失败！");

	}

	@GetMapping("/findBusinessPathInfo")
	public ResponseMap<List<InterfaceInfo>> findBusinessPathInfo() {
		return ResponseMap.getSuccessResult(businessService.findBusinessPathInfo());
	}

	@PostMapping("/test")
	public ResponseMap<String> test(@RequestBody Map<String, Object> values) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		String nodeInfo = values.get("nodeList") + "";
		String lineInfo = values.get("lineList") + "";
		String flowInfo = values.get("flowInfo") + "";

		List<LinkedHashMap<String, Object>> nodeList = objectMapper.readValue(nodeInfo, List.class);
		List<LinkedHashMap<String, Object>> lineList = objectMapper.readValue(lineInfo, List.class);
		FlowInfo flow = objectMapper.readValue(flowInfo, FlowInfo.class);
		List<NodeBaseInfo> list = new ArrayList<>();

		BpmnModel bpmnModel = new BpmnModel();
		Process process = new Process();

		for (LinkedHashMap<String, Object> map : nodeList) {
			NodeInfo info = MapUtil.MapToBean(map, NodeInfo.class);
			list.add(info);
			System.out.println(info);
			process.addFlowElement(FlowElementGenerateUtil.createFlowElement(info));
		}
		for (LinkedHashMap<String, Object> map : lineList) {
			LineInfo info = MapUtil.MapToBean(map, LineInfo.class);
			list.add(info);
			System.out.println(info);
			process.addFlowElement(FlowElementGenerateUtil.createFlowElement(info));
		}

		Document document = XmlUtil.createDocument(flow.getId(), list, 832.4, 1185.2);
		String s = document.asXML();
		System.out.println(s);

		process.setId(flow.getId());
		process.setName(flow.getName());

		bpmnModel.addProcess(process);

		Boolean aBoolean = activitiService.deployNewActiviti(bpmnModel, flow.getName());

		return aBoolean ? ResponseMap.getSuccessResult() : ResponseMap.getFailResult();
	}

	@GetMapping("/findBusinessPathInfos")
	public ResponseMap<List<InterfaceInfo>> findBusinessPathInfo1() {
		return ResponseMap.getSuccessResult(businessService.findBusinessPathInfo());
	}

	@GetMapping("/findAllGroupInfo")
	public ResponseMap<List<GroupInfo>> findAllGroupInfo() {
		return ResponseMap.getSuccessResult(businessService.findAllGroupInfo());
	}

	@GetMapping("/findAllBusinessInfo")
	public ResponseMap<List<BusinessInfo>> findAllBusinessInfo() {
		return ResponseMap.getSuccessResult(businessService.findAllBusinessInfo());
	}
}
