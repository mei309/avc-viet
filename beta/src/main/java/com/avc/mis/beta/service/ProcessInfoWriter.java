/**
 * 
 */
package com.avc.mis.beta.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.avc.mis.beta.dao.DeletableDAO;
import com.avc.mis.beta.dao.ProcessInfoDAO;
import com.avc.mis.beta.dto.basic.ProcessBasic;
import com.avc.mis.beta.dto.link.ProcessManagementDTO;
import com.avc.mis.beta.dto.system.UserMessageDTO;
import com.avc.mis.beta.entities.codes.BasePoCode;
import com.avc.mis.beta.entities.data.UserEntity;
import com.avc.mis.beta.entities.enums.DecisionType;
import com.avc.mis.beta.entities.enums.EditStatus;
import com.avc.mis.beta.entities.enums.ManagementType;
import com.avc.mis.beta.entities.enums.MessageLabel;
import com.avc.mis.beta.entities.enums.ProcessName;
import com.avc.mis.beta.entities.enums.ProcessStatus;
import com.avc.mis.beta.entities.link.ProcessManagement;
import com.avc.mis.beta.entities.process.GeneralProcess;
import com.avc.mis.beta.entities.system.UserMessage;
import com.avc.mis.beta.repositories.ProcessInfoRepository;

/**
 * @author Zvi
 *
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class ProcessInfoWriter {
	
	@Autowired private ProcessInfoDAO dao;
	
	@Autowired private DeletableDAO deletableDAO;
	
	@Autowired private ProcessReader processReader;
	@Autowired private ProcessInfoReader processInfoReader;
	
	@Autowired private ProcessInfoRepository processInfoRepository;


	@Deprecated
	public void removeUserMessages(Integer userId) {
		List<UserMessageDTO> userMessages = processInfoReader.getAllUserMessages(userId, null, null);
		userMessages.forEach(m -> deletableDAO.permenentlyRemoveEntity(UserMessage.class, m.getId()));

	}
	
	
	/**
	 * Sets an alert to be sent to the given user, when a process of the given type is added or edited.
	 * @param userId the id of UserEntity to be notified
	 * @param processName the name of the type of process to notify for.
	 * @param managementType type of approval needed from user for given process type.
	 * @return id of the newly added ProcessManagement
	 */
	public Integer addProcessTypeAlert(Integer userId, ProcessName processName, ManagementType managementType) {
		ProcessManagement processTypeAlert = new ProcessManagement();
		processTypeAlert.setProcessType(dao.getProcessTypeByValue(processName));
		processTypeAlert.setManagementType(managementType);
		deletableDAO.addEntity(processTypeAlert, UserEntity.class, userId);
		return processTypeAlert.getId();
	}
	
	public void removeProcessTypeAlert(Integer processTypeAlertId) {
		ProcessManagementDTO processTypeAlert = processInfoReader.getProcessTypeAlert(processTypeAlertId);
		String title = "You where removed from getting alerts on " + processTypeAlert.getProcessName();
		dao.addMessage(processTypeAlert.getUser().getId(), null, title);
		deletableDAO.permenentlyRemoveEntity(ProcessManagement.class, processTypeAlert.getId());
	}
	
	/**
	 * Approve (or any other decision) to a approval task for a process, including snapshot of process state approved.
	 * @param processId the process id.
	 * @param decisionType the decision made.
	 * @param processSnapshot snapshot of the process as seen by the approver.
	 * @param remarks
	 * @throws IllegalArgumentException trying to approve for another user.
	 */
	public void setUserProcessDecision(int processId, DecisionType decisionType, String processSnapshot, String remarks) {
		dao.setUserProcessDecision(processId, decisionType, processSnapshot, remarks);		
	}
	
	/**
	 * Sets the process status for the process life cycle. e.g. FINAL - process items show in inventory
	 * @param processStatus the process state life cycle of the process.
	 * @param processId
	 */
	public void setProcessStatus(ProcessStatus processStatus, Integer processId) {
		dao.setProcessStatus(processStatus, processId);
	}
	
	/**
	 * Sets the record status for the process life cycle. e.g. LOCKED - information of the process can't be edited
	 * @param editStatus the editing state life cycle of the process.
	 * @param processId
	 */
	public void setEditStatus(EditStatus editStatus, Integer processId) {
		dao.setEditStatus(editStatus, processId);
	}
	
	/**
	 * Changes the label of the message. e.g. from NEW to SEEN
	 * @param messageId
	 * @param labelName
	 */
	public void setMessageLabel(int messageId, MessageLabel labelName) {
		dao.setMessageLabel(messageId, labelName);
	}
	
	public void removeProcess(Integer processId) {
		ProcessBasic processBasic = processReader.getProcessesBasic(processId);
		removeProcess(processId, processBasic.getProcessClazz());
	}
	
	private void removeProcess(Integer processId, Class<? extends GeneralProcess> clazz) {
		deletableDAO.permenentlyRemoveEntity(clazz, processId);
	}

	public List<String> removeAllProcesses(Integer poCodeId) {
		List<ProcessBasic<GeneralProcess>> processes = processReader.getAllProcessesByPo(poCodeId);
		List<String> removed = processes.stream().map(i -> "removing process: " + i).collect(Collectors.toList());
		processes.forEach(i -> removeProcess(i.getId(), i.getProcessClazz()));
		//delete po code
		deletableDAO.permenentlyRemoveEntity(BasePoCode.class, poCodeId);
		return removed;
	}
	
	
}
