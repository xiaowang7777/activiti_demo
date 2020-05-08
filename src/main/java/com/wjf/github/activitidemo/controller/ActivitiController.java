package com.wjf.github.activitidemo.controller;

import com.wjf.github.activitidemo.config.FileConfigProperties;
import com.wjf.github.activitidemo.entity.ActivitiTaskVariablesInfo;
import com.wjf.github.activitidemo.entity.HistoryTaskInfo;
import com.wjf.github.activitidemo.entity.ProcessDefinitionInfo;
import com.wjf.github.activitidemo.entity.TaskNormalInfo;
import com.wjf.github.activitidemo.service.ActivitiService;
import com.wjf.github.activitidemo.util.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.ibatis.annotations.Param;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/activiti")
public class ActivitiController {

	@Autowired
	private ActivitiService activitiService;

	@Autowired
	private FileConfigProperties fileConfigProperties;

	@PostMapping("/userTaskClaim/{taskInfo}/{userInfo}")
	public ResponseMap<String> userTaskClaim(@PathVariable("taskInfo") String taskInfo, @PathVariable("userInfo") String userInfo) {
		byte[] taskByte = Base64.getDecoder().decode(taskInfo.getBytes());
		byte[] userByte = Base64.getDecoder().decode(userInfo.getBytes());
		String taskId = new String(taskByte);
		String userId = new String(userByte);
		activitiService.userTaskClaim(taskId, userId);
		return ResponseMap.getSuccessResult();
	}

	@PostMapping("/userTaskResetAssignee/{taskInfo}")
	public ResponseMap<String> userTaskResetAssignee(@PathVariable("taskInfo") String taskInfo) {
		byte[] taskByte = Base64.getDecoder().decode(taskInfo.getBytes());
		String taskId = new String(taskByte);
		activitiService.userTaskResetAssignee(taskId);
		return ResponseMap.getSuccessResult();
	}

	@PostMapping("/findHistoryActivitiInfo/{processInstanceInfo}")
	public ResponseMap<List> findHistoryActivitiInfo(@PathVariable("processInstanceInfo") String processInstanceInfo) {
		byte[] processInstanceByte = Base64.getDecoder().decode(processInstanceInfo.getBytes());
		String processInstanceId = new String(processInstanceByte);
		return ResponseMap.getSuccessResult(activitiService.findHistoryActivitiInfo(processInstanceId));
	}

	@PostMapping("/findHistoryTaskInfo/{taskInfo}")
	public ResponseMap<HistoryTaskInfo> findHistoryTaskInfo(@PathVariable("taskInfo") String taskInfo) {
		byte[] taskByte = Base64.getDecoder().decode(taskInfo.getBytes());
		String taskId = new String(taskByte);
		return ResponseMap.getSuccessResult(activitiService.findHistoryTaskInfo(taskId));
	}

	@PostMapping("/rollBackUserTask")
	public ResponseMap<String> rollBackUserTask(@RequestBody MyMap<String, Object> values) throws UserTaskDontFindException,
			UserTaskCantRollBackException, UserTaskDontMatchException, IOException, ProcessInstanceDontFindException {
		String processInstanceId = values.eject("processInstanceId") + "";
		String taskId = values.eject("taskId") + "";
		HistoricTaskInstance historicTask = activitiService.getLastUserTaskByProcessInstanceIdAndTaskId(processInstanceId, taskId);
		activitiService.rollBackUserTask(processInstanceId, taskId, historicTask, values);
		return ResponseMap.getSuccessResult();
	}

	@PostMapping("/deployNewActiviti")
	public ResponseMap<String> deployNewActiviti(@Param("file") MultipartFile file) throws IOException {
		String uploadfilepath = CommonsUtils.getUploadfilepath(file, fileConfigProperties.getLocalPath());
		activitiService.deployNewActiviti(fileConfigProperties.getLocalPath() + uploadfilepath, "测试1", "测试1");
		return ResponseMap.getSuccessResult();
	}

	@GetMapping("/getActivityResourceDiagramInputStreamByProcessDefinitionId/{processDefinitionId}")
	public void getActivityResourceDiagramInputStreamByProcessDefinitionId(@PathVariable("processDefinitionId") String processDefinitionId, HttpServletResponse response) throws IOException, TranscoderException, DocumentException {
		response.setContentType("image/png");

		response.setDateHeader("Expires", 0L);
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		response.setHeader("Pragma", "no-cache");

		ServletOutputStream outputStream = null;
		try {
			outputStream = response.getOutputStream();
			activitiService.getActivityResourceDiagramInputStreamByProcessDefinitionId(processDefinitionId, outputStream);
		} catch (IOException | DocumentException | TranscoderException e) {
			e.printStackTrace();
		} finally {
			try {
				if (outputStream != null && !response.isCommitted()) {
					outputStream.flush();
					outputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@GetMapping("/findAllProcessDefinitionInfo")
	public ResponseMap<List<ProcessDefinitionInfo>> findAllProcessDefinitionInfo() {
		return ResponseMap.getSuccessResult(activitiService.findAllProcessDefinitionInfo());
	}

	@GetMapping("/findMyUserTask/{userId}")
	public ResponseMap<List<ActivitiTaskVariablesInfo>> findMyUserTask(@PathVariable("userId") Integer userId) {
		List<ActivitiTaskVariablesInfo> candidate = activitiService.findMySelfUserTaskBusinessByCandidate(userId + "");
		return ResponseMap.getSuccessResult(candidate);
	}

	@GetMapping("/findUserTaskDetailInfo/{taskInfo}")
	public ResponseMap<TaskNormalInfo> findUserTaskDetailInfo(@PathVariable("taskInfo") String taskInfo) {
		byte[] decode = Base64.getDecoder().decode(taskInfo);
		String taskId = new String(decode);
		return ResponseMap.getSuccessResult(activitiService.findUserTaskDetailInfo(taskId));
	}
}
