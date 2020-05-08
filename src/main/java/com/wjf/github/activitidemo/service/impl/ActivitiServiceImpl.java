package com.wjf.github.activitidemo.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wjf.github.activitidemo.entity.*;
import com.wjf.github.activitidemo.mapper.ActivitiMapper;
import com.wjf.github.activitidemo.service.ActivitiService;
import com.wjf.github.activitidemo.util.*;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.*;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.lang3.ArrayUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipInputStream;

/**
 * Activiti工作流操作类
 * 对于完成每个任务节点时的任务信息，应生成一个名为当前任务ID的流程参数
 * 对于每个流程的所关联的业务数据，应生成一个名为'data'的流程参数
 *
 * @author wjf
 */
@Service
public class ActivitiServiceImpl implements ActivitiService {

	private final TaskService taskService;

	private final RepositoryService repositoryService;

	private final RuntimeService runtimeService;

	private final HistoryService historyService;

	private final ActivitiMapper activitiMapper;

	private final ObjectMapper objectMapper;

	private final static Lock LOCK = new ReentrantLock();

	public ActivitiServiceImpl(ProcessEngine processEngine, ActivitiMapper activitiMapper, ObjectMapper objectMapper) {
		this.activitiMapper = activitiMapper;
		this.taskService = processEngine.getTaskService();
		this.repositoryService = processEngine.getRepositoryService();
		this.runtimeService = processEngine.getRuntimeService();
		this.historyService = processEngine.getHistoryService();
		this.objectMapper = objectMapper;
	}

	/**
	 * 部署流程
	 *
	 * @param zipPath  zip文件路径
	 * @param name     工作流名称
	 * @param fileName 文件名
	 * @return 是否部署成功
	 * @throws FileNotFoundException 文件未找到异常
	 */
	@Override
	@Transactional
	public Boolean deployNewActiviti(String zipPath, String name, String fileName) throws FileNotFoundException {

		//读取文件并部署流程
		File file = new File(zipPath);
		if (!file.exists()) {
			return false;
		}
		InputStream inputStream = new FileInputStream(file);
		ZipInputStream zipInputStream = new ZipInputStream(inputStream, Charset.forName("GBK"));
		Deployment deploy = repositoryService
				.createDeployment()
				.addZipInputStream(zipInputStream)
				.name(name)
				.deploy();
		return deploy != null;
	}

	@Override
	public Boolean deployNewActiviti(BpmnModel bpmnModel, String deployName) {

		Deployment deploy = repositoryService.createDeployment()
				.addBpmnModel(deployName + ".bpmn", bpmnModel)
				.name(deployName)
				.key(deployName)
				.deploy();

		//读取文件并部署流程
		return deploy != null;
	}

	/**
	 * 开始流程
	 *
	 * @param processDefinitionId    流程定义ID
	 * @param processValue           流程参数
	 * @param businessValue          业务参数
	 * @param businessId             用于多个函数运行时的数据交互的容器，存储业务数据的主键（必须是线程安全的容器）
	 * @param operationTableId       操作的业务数据表
	 * @param businessMethod         业务函数
	 * @param priorValues            前验条件参数
	 * @param priorVerification      前验条件函数
	 * @param posteriorValues        后验条件参数
	 * @param posterioriVerification 后验条件函数
	 * @return 返回业务所需的返回值
	 * @throws PriorConditionDontSatisfyException      前验条件不满足时
	 * @throws PosterioriConditionDontSatisfyException 后验条件不满足时
	 */
	@Override
	@Transactional
	public <T> StartProcessReturnInfo<T> startTechnologicalProcess(String processDefinitionId, Map<String, Object> processValue,
	                                                               Map<String, Object> businessValue, List<Integer> businessId, Integer operationTableId,
	                                                               GetObjectByValue<T> businessMethod, Map<String, Object> priorValues, BusinessDataVerification priorVerification,
	                                                               Map<String, Object> posteriorValues, BusinessDataVerification posterioriVerification) throws PriorConditionDontSatisfyException, PosterioriConditionDontSatisfyException, ProcessInstanceDontFindException, IOException, KindCantUseException, StartEventDontHaveAnyOutGoingFlowsException, UserTaskDontFindException {

		//		前验条件
		if (!priorVerification.execute(priorValues)) {
			throw new PriorConditionDontSatisfyException();
		}
		Map<String, Object> values = new HashMap<>();

		Process process = repositoryService.getBpmnModel(processDefinitionId)
				.getProcesses()
				.get(0);

		StartEvent startEvent = process.findFlowElementsOfType(StartEvent.class).get(0);
		UserTask userTask = getOperationUserStatus(startEvent, values);
		setCandidateUsers(userTask, values);

		System.out.println(values);

//		开启流程
		ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId, values);

		T execute = businessMethod.execute(businessValue);

		if (processValue != null) {
			setVariablesByProcessInstance(processInstance.getProcessInstanceId(), processValue);
		}

//      后验条件
		if (!posterioriVerification.execute(posteriorValues)) {
			throw new PosterioriConditionDontSatisfyException();
		}

//		用于记录流程操作的是哪个表
		activitiMapper.createNewProcessInstanceBusinessInfo(processInstance.getProcessInstanceId(), operationTableId, businessId.get(0));

		StartProcessReturnInfo<T> returnInfo = new StartProcessReturnInfo<>();

		returnInfo.setData(execute);
		returnInfo.setProcessInstanceId(processInstance.getProcessInstanceId());

		return returnInfo;
	}


	/**
	 * 完成任务（个人任务，需要手动指定下一个任务执行人）
	 * 返回业务函数 {@param businessMethod} 执行返回的 {@param <T>} 类型的值
	 *
	 * @param taskId                要完成的任务编号
	 * @param operationUserIdNow    当前任务执行人员编号
	 * @param processValue          需要设置的流程参数
	 * @param businessValue         业务处理的参数
	 * @param businessMethod        业务处理的方法
	 * @param priorValues           前验函数的参数
	 * @param priorVerification     前验函数的方法
	 * @param posteriorValues       后验函数的参数
	 * @param posteriorVerification 后验函数的方法
	 * @param <T>                   业务完成时的返回值类型
	 * @return 业务完成时的的返回值
	 * @throws UserTaskDontMatchException              用户任务未找到
	 * @throws PriorConditionDontSatisfyException      前验函数不满足
	 * @throws PosterioriConditionDontSatisfyException 后验函数不满足
	 * @throws JsonProcessingException                 Jackson异常
	 */
	@Override
	@Transactional
	public <T> T completeUserTask(String taskId, Integer operationUserIdNow, Map<String, Object> processValue,
	                              Map<String, Object> businessValue, GetObjectByValue<T> businessMethod,
	                              Map<String, Object> priorValues, BusinessDataVerification priorVerification,
	                              Map<String, Object> posteriorValues, BusinessDataVerification posteriorVerification)
			throws UserTaskDontMatchException,
			PriorConditionDontSatisfyException,
			PosterioriConditionDontSatisfyException,
			IOException,
			ProcessInstanceDontFindException,
			NumberFormatException, UserTaskDontFindException {

//		业务逻辑处理前的对数据的前验条件判断
		if (!priorVerification.execute(priorValues)) {
			throw new PriorConditionDontSatisfyException();
		}

//		获取要完成的用户任务对象
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			throw new UserTaskDontFindException();
		}
//		验证处理人员身份是否正确
		if (!task.getAssignee().equals(operationUserIdNow + "")) {
			throw new UserTaskDontMatchException();
		}

		String userTaskId = task.getId();
		String processInstanceId = task.getProcessInstanceId();

//		保存业务执行前的历史数据
		BusinessTableInfo businessTableInfo = activitiMapper.findBusinessTableInfoByProcessInstanceId(processInstanceId);
		String pkName = activitiMapper.findPkNameByTable(businessTableInfo.getBusinessTableName());
		saveHistory(businessTableInfo.getBusinessTableName(), pkName, businessTableInfo.getPk(), processInstanceId, userTaskId);
//		执行业务逻辑
		T execute = businessMethod.execute(businessValue);
//		业务完成后的后验条件
		if (!posteriorVerification.execute(posteriorValues)) {
			throw new PosterioriConditionDontSatisfyException();
		}

		Map<String, Object> map;

		if (processValue != null) {
			map = processValue;
		} else {
			map = new HashMap<>();
		}

		UserTask userTask = getOperationUserStatus(taskId, processValue);
		setCandidateUsers(userTask, map);

//		完成任务
		taskService.complete(taskId, map);
		return execute;
	}

	/**
	 * 生成任务流程参数
	 * 将任务处理的参数生成一个 {@link Map} 的对象，并以 {@param taskId} 为key值，处理参数对象为value值
	 *
	 * @param taskId 任务编号
	 * @param t      处理参数
	 * @param <T>    处理参数的类型
	 * @return 返回可以插入任务流程中的 {@link Map} 对象
	 */
	@Override
	public <T> Map<String, Object> getVariablesByBusiness(String taskId, T t) {
		VariablesInfo<T> variablesInfo = new VariablesInfo<>();
		variablesInfo.setTaskId(taskId);
		variablesInfo.setCreateTime(new Date());
		variablesInfo.setDataObject(t);
		Map<String, Object> map = new HashMap<>();
		map.put(taskId, variablesInfo);
		return map;
	}

	/**
	 * 回滚历史任务
	 * 并还原历史数据
	 *
	 * @param processInstanceId 任务的流程实例ID
	 * @param taskId            当前任务的ID
	 * @param historyTask       历史任务的对象
	 * @throws UserTaskDontFindException  当使用 {@param taskId} 查找用户任务未找到时，
	 *                                    或 {@param historyTask} 为空时抛出
	 * @throws UserTaskDontMatchException 当使用 {@param taskId} 查找的用户任务或者 {@param historyTask}
	 *                                    不与 {@param processInstanceId} 一致时抛出
	 * @throws IOException                IO流异常
	 */
	@Override
	@Transactional
	public void rollBackUserTask(String processInstanceId, String taskId, HistoricTaskInstance historyTask, Map<String, Object> processValue) throws UserTaskDontFindException, UserTaskDontMatchException, IOException {

//		获取当前任务
		Task nowTask = taskService.createTaskQuery().taskId(taskId).singleResult();
//		当当前任务或者历史任务为空时，抛出未找到对应任务异常
		if (nowTask == null || historyTask == null) {
			throw new UserTaskDontFindException();
		}
		runtimeService.setVariables(nowTask.getProcessInstanceId(), processValue);
//		当当前任务和历史任务及传入的流程实例ID不一致时，抛出异常
		if (!nowTask.getProcessInstanceId().equals(historyTask.getProcessInstanceId()) && !processInstanceId.equals(nowTask.getProcessInstanceId())) {
			throw new UserTaskDontMatchException("当前任务和指定的回退任务不在同一个流程中！");
		}

//		获取回滚任务时，回滚历史数据需要的信息，包括操作表、操作表主键、操作数据的主键编号、历史数据的JSON字符串
//		该函数需要传入当前的流程实例编号和查询的历史任务的主键
		HistoryTableInfo businessHistoryData = activitiMapper.findBusinessHistoryData(processInstanceId, historyTask.getId());

		String pkName = activitiMapper.findPkNameByTable(businessHistoryData.getBusinessTableName());
//		保存当前的数据
		saveHistory(businessHistoryData.getBusinessTableName(), pkName, businessHistoryData.getPk(), processInstanceId, historyTask.getId());

//		历史数据
		Map<String, Object> map = objectMapper.readValue(businessHistoryData.getHistoryData(), Map.class);

//		拼接回滚历史数据的SQL语句
		StringBuilder builder = new StringBuilder("UPDATE ");
		builder.append(businessHistoryData.getBusinessTableName()).append(" SET ");
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			String s = value.toString();
			if (key.endsWith("time")) {
				s = s.substring(0, s.lastIndexOf("+"));
			}
			if (key.equals(pkName)) {
				continue;
			}
			builder.append(key).append("='").append(s).append("',");
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.append(" WHERE ").append(pkName).append("=").append(map.get(pkName));
		String updateSql = builder.toString();

//		回滚历史数据
		activitiMapper.executeUpdateSql(updateSql);

//		获取流程信息
		BpmnModel bpmnModel = repositoryService.getBpmnModel(historyTask.getProcessDefinitionId());
		Process process = bpmnModel.getProcesses().get(0);

//		获取流程中所有的用户任务节点
		List<UserTask> userTaskList = process.findFlowElementsOfType(UserTask.class);

//		获取当前任务和历史任务的定义信息
		UserTask nowUserTask = null;
		UserTask historyUserTask = null;
		for (UserTask userTask : userTaskList) {
			if (userTask.getId().equals(nowTask.getTaskDefinitionKey())) {
				nowUserTask = userTask;
			}
			if (historyTask.getTaskDefinitionKey().equals(userTask.getId())) {
				historyUserTask = userTask;
			}
		}
//		当未找到当前任务或历史任务的定义信息时，抛出异常
		if (nowUserTask == null || historyUserTask == null) {
			throw new UserTaskDontFindException("未查找到用户任务的定义信息！");
		}

//		获取当前任务节点的所有出线信息
		List<SequenceFlow> outgoingFlows = nowUserTask.getOutgoingFlows();
//		重置当前任务节点的出线信息
		nowUserTask.setOutgoingFlows(new ArrayList<>());
//		重新定义连线信息，以当前任务节点开始，指向要退回的历史任务节点，然后将该连线设置为当前任务节点的出线
		List<SequenceFlow> sequenceFlows = new ArrayList<>();
		SequenceFlow sequenceFlow = new SequenceFlow();
		sequenceFlow.setSourceFlowElement(nowUserTask);
		sequenceFlow.setTargetFlowElement(historyUserTask);
		sequenceFlows.add(sequenceFlow);
		nowUserTask.setOutgoingFlows(sequenceFlows);

//		获取任务执行的用户组，并完成当前任务
		Map<String, Object> tmp = new HashMap<>();
		setCandidateUsers(historyUserTask, tmp);
		taskService.complete(nowTask.getId(), tmp);

//		复原原本的流程
		nowUserTask.setOutgoingFlows(outgoingFlows);

	}

	/**
	 * 根据流程实例编号获取所有的用户历史任务信息
	 *
	 * @param processInstanceId 流程定义编号
	 * @return 返回所有的用户历史任务信息
	 */
	@Override
	public List<HistoricTaskInstance> findAllHistoryUserTask(String processInstanceId) {
		return historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list();
	}

	/**
	 * 根据流程实例编号获取最后完成的一个历史任务信息
	 *
	 * @param processInstanceId 流程定义编号
	 * @return 查找到的最后完成的一个历史任务信息
	 */
	@Override
	public HistoricTaskInstance getLastUserTaskByProcessInstanceId(String processInstanceId) {
		return historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(processInstanceId)
				.orderByHistoricTaskInstanceEndTime()
				.desc()
				.list()
				.get(0);
	}

	/**
	 * 获取本次任务的上一个任务的最后一次执行的任务信息
	 *
	 * @param processInstanceId 流程定义编号
	 * @param taskId            任务编号
	 * @return 任务执行历史信息
	 * @throws UserTaskCantRollBackException 任务无法回滚时抛出错误（即该任务为第一级任务）
	 * @throws UserTaskDontFindException     任务未找到时抛出异常
	 */
	@Override
	public HistoricTaskInstance getLastUserTaskByProcessInstanceIdAndTaskId(String processInstanceId, String taskId) throws UserTaskCantRollBackException, UserTaskDontFindException, ProcessInstanceDontFindException {
		List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(processInstanceId)
				.orderByHistoricTaskInstanceEndTime()
				.desc()
				.list();
		Task task = taskService.createTaskQuery()
				.processInstanceId(processInstanceId)
				.taskId(taskId)
				.singleResult();

		if (list == null || list.size() == 0) {
			throw new ProcessInstanceDontFindException();
		}

		if (task == null) {
			throw new UserTaskDontFindException();
		}

		Process process = repositoryService.getBpmnModel(task.getProcessDefinitionId())
				.getProcesses()
				.get(0);
		List<UserTask> userTasks = process.findFlowElementsOfType(UserTask.class);

		UserTask sourceTask = null;

		for (UserTask userTask : userTasks) {
			if (userTask.getId().equals(task.getTaskDefinitionKey())) {
				List<SequenceFlow> incomingFlows = userTask.getIncomingFlows();
				for (SequenceFlow incomingFlow : incomingFlows) {
					if (incomingFlow.getSourceFlowElement() instanceof StartEvent) {
						throw new UserTaskCantRollBackException();
					} else if (incomingFlow.getSourceFlowElement() instanceof UserTask) {
						sourceTask = (UserTask) incomingFlow.getSourceFlowElement();
						break;
					} else if (incomingFlow.getSourceFlowElement() instanceof ExclusiveGateway) {
						getUserTask((ExclusiveGateway) incomingFlow.getSourceFlowElement());
					}
				}
			}
		}

		if (sourceTask == null || list == null) {
			return null;
		}

		HistoricTaskInstance finalRes = null;
		for (HistoricTaskInstance historicTaskInstance : list) {
			if (historicTaskInstance.getTaskDefinitionKey().equals(sourceTask.getId())) {
				finalRes = historicTaskInstance;
				break;
			}
		}
		return finalRes;
	}

	/**
	 * 设置流程变量
	 *
	 * @param processInstanceId 流程实例编号
	 * @param variables         流程变量
	 * @throws ProcessInstanceDontFindException 当通过 {@param processInstanceId} 获取流程实例为空时抛出
	 */
	@Override
	public void setVariablesByProcessInstance(String processInstanceId, Map<String, Object> variables) throws ProcessInstanceDontFindException {
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		if (processInstance == null) {
			throw new ProcessInstanceDontFindException();
		}
		runtimeService.setVariables(processInstanceId, variables);
	}

	/**
	 * 获取流程变量
	 *
	 * @param processInstanceId 流程实例编号
	 * @return 返回当前流程实例的所有流程变量
	 * @throws ProcessInstanceDontFindException 当通过 {@param processInstanceId} 获取流程实例为空时抛出
	 */
	@Override
	public Map<String, Object> getVariablesByProcessInstance(String processInstanceId) throws ProcessInstanceDontFindException {
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		if (processInstance == null) {
			throw new ProcessInstanceDontFindException();
		}
		return runtimeService.getVariables(processInstanceId);
	}

	/**
	 * 获取活动流程图片
	 *
	 * @param processInstanceId 流程实例编号
	 * @param outputStream      写入到的输出流
	 * @throws TranscoderException {@link org.apache.batik} 工具抛出的异常，在从输入流中向输出流写入数据是抛出
	 * @throws DocumentException   {@link org.dom4j} 工具抛出的异常，读取数据时抛出
	 * @throws IOException         IO流异常
	 */
	@Override
	public void getActivityResourceDiagramInputStream(String processInstanceId, OutputStream outputStream) throws TranscoderException, DocumentException, IOException, ProcessInstanceDontFindException {

//		获取流程实例对象
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

		String processDefinitionId = null;
		if (processInstance != null) {
			processDefinitionId = processInstance.getProcessDefinitionId();
		} else {
			processDefinitionId = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult().getProcessDefinitionId();
		}

//		获取BPMN对象
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

		if (bpmnModel != null && bpmnModel.getLocationMap().size() > 0) {
//			获取流程图画图对象
			ProcessDiagramGenerator processDiagramGenerator = new DefaultProcessDiagramGenerator();
//			生成流程图的流
//			注意：1.此时获取到的流中的数据是一个xml文件的数据，但该xml文件的根节点为svg，能在浏览器中直接渲染成流程图片，也可以转换成png格式的图片
//			2.如果流程图中包含的有中文文字信息，则获取流时必须指明支持的字体，否则将会使用默认的字体--Arial，在该字体下会出现中文乱码的情况
			InputStream inputStream = processDiagramGenerator.generateDiagram(bpmnModel, runtimeService.getActiveActivityIds(processInstanceId), Collections.emptyList(), "宋体", "宋体", "宋体");
			getImg(inputStream, outputStream, Base64.getEncoder().encodeToString(processDefinitionId.getBytes()));
		}
	}

	@Override
	public void getActivityResourceDiagramInputStreamByProcessDefinitionId(String processDefinitionId, OutputStream outputStream) throws DocumentException, IOException, TranscoderException {
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
		if (bpmnModel != null && bpmnModel.getLocationMap().size() > 0) {
//			获取流程图画图对象
			ProcessDiagramGenerator processDiagramGenerator = new DefaultProcessDiagramGenerator();
//			生成流程图的流
//			注意：1.此时获取到的流中的数据是一个xml文件的数据，但该xml文件的根节点为svg，能在浏览器中直接渲染成流程图片，也可以转换成png格式的图片
//			2.如果流程图中包含的有中文文字信息，则获取流时必须指明支持的字体，否则将会使用默认的字体--Arial，在该字体下会出现中文乱码的情况
			InputStream inputStream = processDiagramGenerator.generateDiagram(bpmnModel, Collections.emptyList(), Collections.emptyList(), "宋体", "宋体", "宋体");
			getImg(inputStream, outputStream, Base64.getEncoder().encodeToString(processDefinitionId.getBytes()));
		}
	}

	/**
	 * 获取流程定义的流程图
	 *
	 * @param processDefinitionId 获取流程定义编号
	 * @return 返回对应的流程图的文件流
	 */
	@Override
	public InputStream getStaticResourceDiagramInputStream(String processDefinitionId) {
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
		return repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getDiagramResourceName());
	}

	/**
	 * 获取用户的个人任务--任务信息
	 *
	 * @param userId   查询人员编号
	 * @param pageNum  页码
	 * @param pageSize 每页数量
	 * @return 对应的任务信息
	 */
	@Override
	public MyPage<Task> findMySelfUserTask(Integer userId, Integer pageNum, Integer pageSize) {
		List<Task> tasks = taskService.createTaskQuery().taskAssignee(userId + "").orderByTaskCreateTime().desc().listPage(pageNum, pageSize);
		Integer numCount = tasks == null ? 0 : tasks.size();
		return this.getPage(pageNum, pageSize, tasks, numCount);
	}

	/**
	 * 获取用户的个人任务--业务信息
	 *
	 * @param userId   查询人员编号
	 * @param pageNum  页码
	 * @param pageSize 每页数量
	 * @return 对应的任务信息
	 */
	@Override
	public List<ActivitiTaskVariablesInfo> findMySelfUserTaskBusiness(Integer userId, Integer pageNum, Integer pageSize) {
		return activitiMapper.findMySelfTaskAndVariables(userId + "");
	}

	@Override
	public MyPage<Task> findMySelfUserTaskByCandidate(String group, Integer pageNum, Integer pageSize) {
		List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(group).orderByTaskCreateTime().desc().listPage(pageNum, pageSize);
		Integer numCount = tasks == null ? 0 : tasks.size();
		return this.getPage(pageNum, pageSize, tasks, numCount);
	}

	@Override
	public List<ActivitiTaskVariablesInfo> findMySelfUserTaskBusinessByCandidate(String userId) {
		return activitiMapper.findMySelfUserTaskBusinessByCandidate(userId);
	}

	/**
	 * 获取某个流程中自己正在执行的个人任务
	 *
	 * @param processInstanceId 流程实例编号
	 * @param userId            执行人员编号
	 * @return 返回正在执行的个人任务信息
	 */
	@Override
	public Task getMyTaskByProcessInstanceId(@NotNull String processInstanceId, @NotNull Integer userId) {
		return taskService.createTaskQuery().processInstanceId(processInstanceId).taskAssignee(userId + "").singleResult();
	}

	/**
	 * 通过任务编号，判断某个流程是否已经结束
	 *
	 * @param taskId 任务编号
	 * @return 是否已执行完毕
	 * @throws ProcessInstanceDontFindException 未查找到对应的流程
	 */
	@Override
	public Boolean judgeProcessInstanceIsCompleteByTaskId(@NotNull String taskId) throws ProcessInstanceDontFindException {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		ProcessInstance processInstance = runtimeService
				.createProcessInstanceQuery()
				.processInstanceId(task.getProcessInstanceId())
				.singleResult();
		HistoricProcessInstance historicProcessInstance = historyService
				.createHistoricProcessInstanceQuery()
				.processInstanceId(task.getProcessInstanceId())
				.singleResult();

		if (processInstance == null && historicProcessInstance != null) {
			return true;
		}
		if (historicProcessInstance == null) {
			throw new ProcessInstanceDontFindException();
		}
		return false;
	}

	/**
	 * 获取指定人员的历史任务
	 *
	 * @param assignee 执行人员
	 * @param pageNum  页码
	 * @param pageSize 每页数据量
	 * @return 历史任务集合
	 */
	@Override
	public List<ActivitiTaskVariablesInfo> getHistoryTaskByAssignee(@NotNull String assignee, @NotNull Integer pageNum, @NotNull Integer pageSize) {
		return activitiMapper.findHistoryTaskAndVariablesAndProcessInstance(assignee);
	}

	/**
	 * 查询所有的流程定义信息
	 *
	 * @return 返回流程定义信息的列表
	 */
	@Override
	public List<ProcessInfo> findProcessDefinitionList() {
		return activitiMapper.findProcessDefinitionList();
	}

	@Override
	public NowTask findNowTaskInfo(String taskId) throws UserTaskDontFindException {
//		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
//
//		if (task == null) {
//			throw new UserTaskDontFindException();
//		}
//		Integer businessId = activitiMapper.findBusinessIdByProcessInstanceId(task.getProcessInstanceId());
//		String formKey = task.getFormKey();
//
//		System.out.println("formKey--->" + formKey);
//
//		ActivitiStatus list = activitiMapper.findAllJurisdictionByFormKey(formKey);
//
//		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
//		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processInstance.getProcessDefinitionId()).singleResult();
//		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
//		Process process = bpmnModel.getProcesses().get(0);
//		List<UserTask> taskList = process.findFlowElementsOfType(UserTask.class);
//		for (UserTask userTask : taskList) {
//			userTask.getId();
//		}
//
//		NowTask nowTask = new NowTask();
//		nowTask.setNowTaskId(taskId);
//		nowTask.setNowTaskKey(task.getTaskDefinitionKey());
//		nowTask.setUserTasks(taskList);
//		nowTask.setBusinessId(businessId);
//
//		ActivitiStatusVo statusVo = new ActivitiStatusVo();
//		statusVo.setJurisdictionId(list.getJurisdictionId());
//		statusVo.setUpload(list.getUpload());
//		statusVo.setDownStatus(list.getDownStatus().getStatusNumber());
//		statusVo.setUpStatus(list.getUpStatus().getStatusNumber());
//		nowTask.setJurisdiction(statusVo);
//		return nowTask;
		return null;
	}

	@Override
	public String findHistoryBusinessData(String processInstanceId, String variableName) {
		final HistoricVariableInstance historicVariableInstance = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).variableName(variableName).singleResult();
		final Object value = historicVariableInstance.getValue();
		return value.toString();
	}

	@Override
	public String findProcessInstanceIdByTaskId(String taskId) throws UserTaskDontFindException {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			throw new UserTaskDontFindException();
		}
		return task.getProcessInstanceId();
	}

	@Override
	public String findProcessDefinitionIdByTaskId(String taskId) throws UserTaskDontFindException {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			throw new UserTaskDontFindException();
		}
		return task.getProcessDefinitionId();
	}

	@Override
	public HistoryTaskInfo findHistoryTaskInfo(String taskId) {

		HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();

		BpmnModel bpmnModel = repositoryService.getBpmnModel(historicTaskInstance.getProcessDefinitionId());
		Process process = bpmnModel.getProcesses().get(0);
		List<UserTask> userTaskList = process.findFlowElementsOfType(UserTask.class);

		List<String> keyList = new ArrayList<>();
		userTaskList.forEach(userTask -> keyList.add(userTask.getId()));

		HistoryTaskInfo historyTaskInfo = activitiMapper.findHistoryTaskInfo(taskId);
		historyTaskInfo.setTaskDefinitionKeys(keyList);
		return historyTaskInfo;
	}

	@Override
	public List<TaskInfo> findHistoryActivitiInfo(String processInstanceId) {
		return activitiMapper.findHistoryActivitiInfo(processInstanceId);
	}

	/**
	 * 任务拾取
	 *
	 * @param taskId
	 * @param userId
	 */
	@Override
	public void userTaskClaim(String taskId, String userId) {
		taskService.claim(taskId, userId);
	}

	/**
	 * 重置任务执行人
	 *
	 * @param taskId
	 */
	@Override
	public void userTaskResetAssignee(String taskId) {
		taskService.setAssignee(taskId, null);
	}

	/**
	 * 生成开始节点
	 *
	 * @param name
	 * @param id
	 * @return
	 */
	@Override
	public StartEvent getStartEvent(String name, @NotNull String id) {
		StartEvent startEvent = new StartEvent();
		startEvent.setName(name);
		startEvent.setId(id);
		return startEvent;
	}

	/**
	 * 生成用户任务节点
	 *
	 * @param id
	 * @param fromKey
	 * @param candidateUser
	 * @return
	 */
	@Override
	public UserTask getUserTask(@NotNull String id, String fromKey, String candidateUser) {
		UserTask userTask = new UserTask();
		userTask.setId(id);
		userTask.setFormKey(fromKey);
		userTask.setCandidateUsers(Collections.singletonList("#{" + candidateUser + "}"));
		return userTask;
	}

	/**
	 * 生成连线
	 *
	 * @param source
	 * @param target
	 * @param condition
	 * @return
	 */
	@Override
	public SequenceFlow getSequenceFlow(@NotNull String id, String source, String target, String condition) {
		SequenceFlow sequenceFlow = new SequenceFlow();
		sequenceFlow.setId(id);
		sequenceFlow.setSourceRef(source);
		sequenceFlow.setTargetRef(target);
		sequenceFlow.setConditionExpression(condition);
		return sequenceFlow;
	}

	/**
	 * 生成结束节点
	 *
	 * @param name
	 * @param id
	 * @return
	 */
	@Override
	public EndEvent getEndEvent(String name, @NotNull String id) {
		EndEvent endEvent = new EndEvent();
		endEvent.setName(name);
		endEvent.setId(id);
		return endEvent;
	}

	@Override
	public List<ProcessDefinitionInfo> findAllProcessDefinitionInfo() {
		return activitiMapper.findAllProcessDefinitionInfo();
	}

	@Override
	public TaskNormalInfo findUserTaskDetailInfo(String taskId) {
		return activitiMapper.findUserTaskDetailInfo(taskId);
	}

	/**
	 * 获取分页信息
	 *
	 * @param pageNum  当前页码
	 * @param pageSize 每页数据量
	 * @param dataList 数据集合
	 * @param numCount 一共有多少
	 * @param <T>      指定存储的类型
	 * @return 返回分页数据
	 */
	private <T> MyPage<T> getPage(Integer pageNum, Integer pageSize, List<T> dataList, Integer numCount) {
		MyPage<T> page = new MyPage<>();
		page.setPageNum(pageNum);
		page.setPageSize(pageSize);
		page.setData(dataList);
		page.setDataCount(numCount);
		page.setPageCount(numCount % pageSize > 0 ? ((numCount / pageSize) + 1) : (numCount / pageSize));
		return page;
	}

	private String analysisString(String s) {
		return s.replaceAll("\\{", "").replaceAll("}", "").replaceAll("#", "");
	}

	/**
	 * 通过当前任务ID获取下一个任务的定义信息
	 *
	 * @param taskId       用户任务编号
	 * @param processValue 用户任务参数，主要用于判断排他网关的流向
	 * @return 返回下一个任务的定义信息
	 */
	private UserTask getOperationUserStatus(String taskId, Map<String, Object> processValue) throws NumberFormatException, UserTaskDontFindException {
		//获取个人任务信息
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		//根据执行实例编号获取执行实例
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
		//当未获取到流程实例时，此时流程尚未开始，或已经结束了
		if (processInstance == null) {
			return new UserTask();
		}
		//获取流程定义ID
		String processDefinitionId = processInstance.getProcessDefinitionId();
		//获取流程图信息
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
		Process process = bpmnModel.getProcesses().get(0);
		//获取所有的用户任务信息
		List<UserTask> userTasks = process.findFlowElementsOfType(UserTask.class);

		//获取当前任务的任务ID
		String taskDefinitionKey = task.getTaskDefinitionKey();

		//遍历所有的用户任务
		for (UserTask userTask : userTasks) {
			//当遍历到当前任务时
			if (userTask.getId().equals(taskDefinitionKey)) {
				//获取当前任务的所有外连线
				List<SequenceFlow> outgoingFlows = userTask.getOutgoingFlows();
				//遍历所有的出线
				for (SequenceFlow outgoingFlow : outgoingFlows) {
					//获取连线的目标节点
					FlowElement targetFlowElement = outgoingFlow.getTargetFlowElement();
					//当目标节点为用户任务节点时
					if (targetFlowElement instanceof UserTask) {
						return (UserTask) targetFlowElement;
					}
					//当目标节点是排他网关时
					else if (targetFlowElement instanceof ExclusiveGateway) {
						return getUserTaskByExclusiveGateway((ExclusiveGateway) targetFlowElement, processValue);
					}
				}
			}
		}
		throw new UserTaskDontFindException();
	}

	private UserTask getOperationUserStatus(StartEvent startEvent, Map<String, Object> processValue) throws KindCantUseException, StartEventDontHaveAnyOutGoingFlowsException, UserTaskDontFindException {
		List<SequenceFlow> outgoingFlows = startEvent.getOutgoingFlows();

		if (!(outgoingFlows.size() > 0)) {
			throw new StartEventDontHaveAnyOutGoingFlowsException();
		}

		for (SequenceFlow outgoingFlow : outgoingFlows) {
			if (outgoingFlow.getTargetFlowElement() instanceof UserTask) {
				return (UserTask) outgoingFlow.getTargetFlowElement();
			} else if (outgoingFlow.getTargetFlowElement() instanceof ExclusiveGateway) {
				return getUserTaskByExclusiveGateway((ExclusiveGateway) outgoingFlow.getTargetFlowElement(), processValue);
			} else {
				throw new KindCantUseException();
			}
		}
		throw new UserTaskDontFindException();
	}

	/**
	 * 获取排他网关的下一个任务的定义信息
	 *
	 * @param exclusiveGateway 排他网关对象
	 * @param processValue     流程参数
	 * @return 返回排他网关的下一个任务的定义信息
	 */
	private UserTask getUserTaskByExclusiveGateway(ExclusiveGateway exclusiveGateway, Map<String, Object> processValue) throws NumberFormatException, UserTaskDontFindException {
//		获取排他网关的所有外连线
		List<SequenceFlow> outgoingFlows = exclusiveGateway.getOutgoingFlows();
//		遍历所有的外连线
		for (SequenceFlow sequenceFlow : outgoingFlows) {
//			获取外连线的条件表达式
			String conditionExpression = sequenceFlow.getConditionExpression();
//			当表达式的执行结果为true时
			if (Objects.requireNonNull(TrimUtil.getTrimUtil(conditionExpression)).getRes(
					Double.parseDouble(processValue.get(Objects.requireNonNull(TrimUtil.getTrimUtil(conditionExpression)).getFields(conditionExpression)[1]) + ""),
					conditionExpression)) {
//				获取该外连线的目标节点
				FlowElement targetFlowElement1 = sequenceFlow.getTargetFlowElement();
				if (targetFlowElement1 instanceof ExclusiveGateway) {
//					如果目标节点为排他网关时，递归调用
					return getUserTaskByExclusiveGateway((ExclusiveGateway) targetFlowElement1, processValue);
				}
				if (targetFlowElement1 instanceof UserTask) {
//					如果目标节点为用户任务节点时，获取人物节点的执行人，并返回
					return (UserTask) targetFlowElement1;
				}
			}
		}
		throw new UserTaskDontFindException();
	}

	/**
	 * 保存历史数据信息
	 *
	 * @param operationTableName 操作表表名
	 * @param pkName             主键名
	 * @param businessId         业务数据主键编号
	 * @param processInstanceId  流程实例编号
	 * @param userTaskId         用户任务编号
	 * @throws JsonProcessingException Jackson的序列化异常
	 */
	private void saveHistory(String operationTableName, String pkName, Integer businessId, String processInstanceId, String userTaskId) throws JsonProcessingException {

//		拼接查询业务信息的字符串
		String sql = "SELECT * FROM " + operationTableName + " WHERE " + pkName + "=" + businessId;
//		查询业务信息
		Map<String, Object> historyData = activitiMapper.executeSelectSql(sql);
//		将业务数据转换成JSON字符串
		String jsonStr = objectMapper.writeValueAsString(historyData);
//		创建业务信息
		activitiMapper.createNewHistoryDate(jsonStr, processInstanceId, userTaskId);
	}

	private UserTask getUserTask(ExclusiveGateway exclusiveGateway) {
		List<SequenceFlow> incomingFlows = exclusiveGateway.getIncomingFlows();
		for (SequenceFlow incomingFlow : incomingFlows) {
			if (incomingFlow.getSourceFlowElement() instanceof UserTask) {
				return (UserTask) incomingFlow.getSourceFlowElement();
			} else if (incomingFlow.getSourceFlowElement() instanceof ExclusiveGateway) {
				return getUserTask((ExclusiveGateway) incomingFlow.getSourceFlowElement());
			}
		}
		return null;
	}

	private void setCandidateUsers(UserTask userTask, Map<String, Object> map) {
		List<String> users = userTask.getCandidateUsers();
		for (String user : users) {
			String s = analysisString(user);
			List<Integer> list = activitiMapper.findUserListByGroup(s);
			map.put(s, ArrayUtils.toString(list)
					.replaceAll("\\[", "")
					.replaceAll("]", ""));
		}
	}

	private void getImg(InputStream inputStream, OutputStream outputStream, String id) throws DocumentException, IOException, TranscoderException {
//			利用DOM4J和batik将xml的svg数据转换成png图片，并直接写入流中

//			使用DOM4J获取xml中的svg数据
		SAXReader reader = new SAXReader();
		reader.setEncoding("UTF-8");
		Document read = reader.read(inputStream);
		inputStream.close();
		Element rootElement = read.getRootElement();
		String text = rootElement.asXML();

//			定义临时存储流程图数据的文件夹路径
		StringBuilder fileName;
		SimpleDateFormat yearAndMonth = new SimpleDateFormat("yyyy-MM");
		SimpleDateFormat onlyDay = new SimpleDateFormat("dd");
		File file;

		try {
//				为了防止可能存在的文件重名问题，同一时间只能生成一个文件名
			LOCK.lock();
			while (true) {
				Date date = new Date();

				fileName = new StringBuilder();

				fileName.append("C:/processImage/")
						.append(yearAndMonth.format(date))
						.append("/")
						.append(onlyDay.format(date));
				File filePath = new File(fileName.toString());

				if (!filePath.exists()) {
					if (!filePath.mkdirs()) {
						continue;
					}
				}

				fileName.append("/流程图信息-")
						.append(id)
						.append("-")
						.append(date.getTime())
						.append(".txt");
				System.out.println(fileName);
				file = new File(fileName.toString());
				if (!file.exists()) {
					if (file.createNewFile()) {
						break;
					}
				}
			}
		} finally {
			LOCK.unlock();
		}

//			将xml中的svg数据写入临时文件中
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		fileOutputStream.write(text.getBytes(StandardCharsets.UTF_8));
		fileOutputStream.flush();
		fileOutputStream.close();

//			使用batik将svg文件信息转换成png图片。并写入流中
		Transcoder transcoder = new PNGTranscoder();
		TranscoderInput input = new TranscoderInput(new FileInputStream(file));
		TranscoderOutput output = new TranscoderOutput(outputStream);
		transcoder.transcode(input, output);
		input.getInputStream().close();
	}

}
