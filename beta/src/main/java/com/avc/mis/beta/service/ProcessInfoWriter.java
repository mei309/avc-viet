/**
 * 
 */
package com.avc.mis.beta.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.avc.mis.beta.dao.DeletableDAO;
import com.avc.mis.beta.dao.ProcessInfoDAO;
import com.avc.mis.beta.dto.data.UserMessageDTO;
import com.avc.mis.beta.entities.data.ProcessManagement;
import com.avc.mis.beta.entities.data.UserEntity;
import com.avc.mis.beta.entities.enums.DecisionType;
import com.avc.mis.beta.entities.enums.ManagementType;
import com.avc.mis.beta.entities.enums.MessageLabel;
import com.avc.mis.beta.entities.enums.ProcessName;
import com.avc.mis.beta.entities.enums.RecordStatus;
import com.avc.mis.beta.entities.process.ApprovalTask;
import com.avc.mis.beta.entities.process.UserMessage;

/**
 * @author Zvi
 *
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class ProcessInfoWriter {
	
	@Autowired private ProcessInfoDAO dao;
	
	@Autowired private DeletableDAO deletableDAO;
	
	@Autowired private ProcessInfoReader processInfoReader;


	@Deprecated
	public void removeUserMessages(Integer userId) {
		List<UserMessageDTO> userMessages = processInfoReader.getAllUserMessages(userId);
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
	
	public void editProcessTypeAlert(ProcessManagement processTypeAlert, ManagementType managementType) {
		processTypeAlert.setManagementType(managementType);
		deletableDAO.editEntity(processTypeAlert);
	}
	
	public void removeProcessTypeAlert(Integer processTypeAlertId) {
		ProcessManagement processTypeAlert = processInfoReader.getProcessTypeAlert(processTypeAlertId);
		String title = "You where removed from getting alerts on " + processTypeAlert.getProcessType().getValue();
		dao.addMessage(processTypeAlert.getUser(), null, title);
		deletableDAO.permenentlyRemoveEntity(processTypeAlert);
	}

	/**
	 * Approve (or any other decision) to a approval task for a process.
	 * ATTENTION! Should not be used because ApprovalTask user can be changed with current user, 
	 * and will be approved since user isn't updated in the database.
	 * @param approval the approval task with id and user with id.
	 * @param decisionType the decision made.
	 * @throws IllegalArgumentException trying to approve for another user.
	 */
	@Deprecated
	public void approveProcess(ApprovalTask approval, String decisionType) {
		dao.approveProcess(approval, decisionType);
	}
	
	/**
	 * Approve (or any other decision) to a approval task for a process, including snapshot of process state approved.
	 * @param approvalId the ApprovalTask id.
	 * @param decisionType the decision made.
	 * @param processSnapshot snapshot of the process as seen by the approver.
	 * @param remarks
	 * @throws IllegalArgumentException trying to approve for another user.
	 */
	public void setProcessDecision(int approvalId, DecisionType decisionType, String processSnapshot, String remarks) {
		dao.setProcessDecision(approvalId, decisionType, processSnapshot, remarks);		
	}
	
	/**
	 * Sets the record status for the process life cycle. e.g. LOCKED - information of the process can't be edited
	 * @param recordStatus the editing/state life cycle of the process.
	 * @param processId
	 */
	public void setProcessRecordStatus(RecordStatus recordStatus, Integer processId) {
		dao.setProcessRecordStatus(recordStatus, processId);
	}
	
	/**
	 * Changes the label of the message. e.g. from NEW to SEEN
	 * @param messageId
	 * @param labelName
	 */
	public void setMessageLabel(int messageId, MessageLabel labelName) {
		dao.setMessageLabel(messageId, labelName);
	}

	
	
}
