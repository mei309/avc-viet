/**
 * 
 */
package com.avc.mis.beta.dto.system;

import java.time.Instant;

import com.avc.mis.beta.dto.GeneralInfoDTO;
import com.avc.mis.beta.entities.BaseEntity;
import com.avc.mis.beta.entities.enums.DecisionType;
import com.avc.mis.beta.entities.enums.ProcessName;
import com.avc.mis.beta.entities.system.ApprovalTask;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO(Data Access Object) for sending or displaying ApprovalTask entity data.
 * 
 * @author Zvi
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ApprovalTaskDTO extends GeneralInfoDTO {

	private String userName;
	private String decisionType;
	private String processSnapshot;
	
	public ApprovalTaskDTO(Integer id, Integer version, 
			String poCodes, String suppliers,
			String title, Integer processId, ProcessName processName, String processType, 
			Instant createdDate, String modifiedBy, String userName, DecisionType decision, String processSnapshot) {
		super(id, version, 
				poCodes, suppliers,
				title, processId, processName, processType, 
				createdDate, modifiedBy);
		this.userName = userName;
		this.decisionType = decision.name();
		this.processSnapshot = processSnapshot;
	}
	
	@Override
	public Class<? extends BaseEntity> getEntityClass() {
		return ApprovalTask.class;
	}
}
