/**
 * 
 */
package com.avc.mis.beta.dto.data;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

import com.avc.mis.beta.dto.BaseDTOWithVersion;
import com.avc.mis.beta.entities.data.Staff;
import com.avc.mis.beta.entities.enums.ProcessType;
import com.avc.mis.beta.entities.process.ProcessStatus;
import com.avc.mis.beta.entities.process.ProductionLine;
import com.avc.mis.beta.entities.process.ProductionProcess;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @author Zvi
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ProductionProcessDTO extends BaseDTOWithVersion {
//	@EqualsAndHashCode.Exclude
//	private Integer id;
	private Instant insertTime;
	private Staff staffRecording;
	private Integer poId;
	private ProcessType processType;
	private ProductionLine productionLine;
	private LocalDateTime time;
	private Duration duration;
	private Integer numOfWorkers;
	private ProcessStatus status;
	private String remarks;
	
	public ProductionProcessDTO(Integer id, Long version, Instant insertTime, 
			Staff staffRecording, Integer poId, ProcessType processType, ProductionLine productionLine, 
			LocalDateTime time, Duration duration, Integer numOfWorkers, ProcessStatus status, 
			String remarks) {
		super(id, version);
		this.insertTime = insertTime;
		this.staffRecording = staffRecording;
		this.poId = poId;
		this.processType = processType;
		this.productionLine = productionLine;
		this.time = time;
		this.duration = duration;
		this.numOfWorkers = numOfWorkers;
		this.status = status;
		this.remarks = remarks;
	}
	
	public ProductionProcessDTO(@NonNull ProductionProcess process) {
		super(process.getId(), process.getVersion());
		this.insertTime = process.getInsertTime();
		this.staffRecording = process.getStaffRecording();
		this.poId = process.getPo().getId();
		this.processType = process.getProcessType();
		this.productionLine = process.getProductionLine();
		this.time = process.getTime();
		this.duration = process.getDuration();
		this.numOfWorkers = process.getNumOfWorkers();
		this.status = process.getStatus();
		this.remarks = process.getRemarks();
	}
}
