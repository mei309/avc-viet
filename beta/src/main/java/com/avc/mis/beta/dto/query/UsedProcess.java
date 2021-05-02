/**
 * 
 */
package com.avc.mis.beta.dto.query;

import com.avc.mis.beta.dto.basic.ProcessBasic;
import com.avc.mis.beta.entities.enums.ProcessName;
import com.avc.mis.beta.entities.process.PoProcess;
import com.avc.mis.beta.entities.process.collection.ProcessParent;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Value;

/**
 * @author zvi
 *
 */
@Value
public class UsedProcess {

	ProcessBasic<? extends PoProcess> usedProcess;

	public UsedProcess(
			Integer usedProcessId, Integer usedProcessVersion, ProcessName processName, Class<? extends PoProcess> ProcessClazz) {
		super();
		this.usedProcess = new ProcessBasic<>(usedProcessId, usedProcessVersion, processName, ProcessClazz);
	}	

	@JsonIgnore
	public ProcessParent getProcessParent() {
		ProcessParent processParent = new ProcessParent();
		processParent.setUsedProcess(getUsedProcess().getProcess());
		return processParent;
	}

}
