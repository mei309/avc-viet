/**
 * 
 */
package com.avc.mis.beta.dto.system;

import java.time.Instant;

import com.avc.mis.beta.dto.GeneralInfoDTO;
import com.avc.mis.beta.entities.BaseEntity;
import com.avc.mis.beta.entities.enums.MessageLabel;
import com.avc.mis.beta.entities.enums.ProcessName;
import com.avc.mis.beta.entities.system.UserMessage;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO(Data Access Object) for sending or displaying UserMessage entity data.
 * 
 * @author Zvi
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class UserMessageDTO extends GeneralInfoDTO {

	private String userName;
	private String label;
	
	public UserMessageDTO(Integer id, Integer version, 
			String poCodes, String suppliers,
			String title, Integer processId, ProcessName processName, String processType, 
			Instant createdDate, String modifiedBy, String userName, MessageLabel label) {
		super(id, version, 
				poCodes, suppliers,
				title, processId, processName, processType, 
				createdDate, modifiedBy);
		this.userName = userName;
		this.label = label.name();
	}
	
	@Override
	public Class<? extends BaseEntity> getEntityClass() {
		return UserMessage.class;
	}
}
