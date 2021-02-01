/**
 * 
 */
package com.avc.mis.beta.dto.embedable;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.OffsetDateTime;

import com.avc.mis.beta.dto.DataDTO;
import com.avc.mis.beta.dto.GeneralProcessDTO;
import com.avc.mis.beta.entities.enums.EditStatus;
import com.avc.mis.beta.entities.enums.ProcessName;
import com.avc.mis.beta.entities.enums.ProcessStatus;
import com.avc.mis.beta.entities.values.ProductionLine;

import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * @author zvi
 *
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class GeneralProcessInfo extends DataDTO {

	Instant createdDate;
	String userRecording;
	ProcessName processName;
	ProductionLine productionLine;
	OffsetDateTime recordedTime;
	LocalTime startTime;
	LocalTime endTime;
	Duration duration;
	Integer numOfWorkers;
	ProcessStatus processStatus;
	EditStatus editStatus;
	String remarks;
	String approvals;

	
	public GeneralProcessInfo(Integer id, Integer version, Instant createdDate, String userRecording, 
			ProcessName processName, ProductionLine productionLine, 
			OffsetDateTime recordedTime, LocalTime startTime, LocalTime endTime, Duration duration, Integer numOfWorkers, 
			ProcessStatus processStatus, EditStatus editStatus,
			String remarks, String approvals) {
		super(id, version);
		this.createdDate = createdDate;
		this.userRecording = userRecording;
		this.processName = processName;
		this.productionLine = productionLine;
		this.recordedTime = recordedTime;
		this.startTime = startTime;
		this.endTime = endTime;
		this.duration = duration;
		this.numOfWorkers = numOfWorkers;
		this.processStatus = processStatus;
		this.editStatus = editStatus;
		this.remarks = remarks;
		this.approvals = approvals;

	}
}