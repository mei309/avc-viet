/**
 * 
 */
package com.avc.mis.beta.dto.process.group;

import com.avc.mis.beta.dto.RankedAuditedDTO;
import com.avc.mis.beta.entities.process.group.ProcessGroup;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO class for ProcessGroup which contains information used by all groups in collection of groups in a processes.
 * 
 * @author zvi
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class ProcessGroupDTO extends RankedAuditedDTO {

	private String groupName;

	@JsonIgnore
	private boolean tableView;

	public ProcessGroupDTO(Integer id, Integer version, Integer ordinal,
			String groupName, boolean tableView) {
		super(id, version, ordinal);
		this.groupName = groupName;
		this.tableView = tableView;
	}	
	
	@Override
	public ProcessGroup fillEntity(Object entity) {
		ProcessGroup processGroup;
		if(entity instanceof ProcessGroup) {
			processGroup = (ProcessGroup) entity;
		}
		else {
			throw new IllegalStateException("Param has to be ProcessGroup class");
		}
		super.fillEntity(processGroup);
		processGroup.setGroupName(getGroupName());
		processGroup.setTableView(isTableView());
		
		return processGroup;
	}
	
}
