/**
 * 
 */
package com.avc.mis.beta.dto.process;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;

import com.avc.mis.beta.dto.ProcessDTO;
import com.avc.mis.beta.entities.enums.EditStatus;
import com.avc.mis.beta.entities.enums.ProcessName;
import com.avc.mis.beta.entities.enums.ProcessStatus;
import com.avc.mis.beta.entities.process.PoProcess;
import com.avc.mis.beta.entities.process.PoTransactionProcess;
import com.avc.mis.beta.entities.processinfo.ProcessItem;
import com.avc.mis.beta.entities.values.ProductionLine;

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
public abstract class PoProcessDTO extends GeneralProcessDTO {
	
	private PoCodeDTO poCode;
		
	public PoProcessDTO(Integer id, Integer version, Instant createdDate, String staffRecording, 
			Integer poCodeId, String contractTypeCode, String contractTypeSuffix, 
			Integer supplierId, Integer supplierVersion, String supplierName, 
			ProcessName processName, ProductionLine productionLine, 
			OffsetDateTime recordedTime, Duration duration, Integer numOfWorkers, ProcessStatus processStatus, EditStatus editStatus,
			String remarks , String approvals) {
		super(id, version, createdDate, staffRecording, 
//				poCodeId, contractTypeCode, contractTypeSuffix,
//				supplierId, supplierVersion, supplierName, 
				processName, productionLine, 
				recordedTime, duration, numOfWorkers, processStatus, editStatus, remarks, approvals);
		this.poCode = new PoCodeDTO(poCodeId, contractTypeCode, contractTypeSuffix, supplierName);
		
	}
	
	public PoProcessDTO(@NonNull PoProcess process) {
		super(process);
		this.poCode = new PoCodeDTO(process.getPoCode());
	}
	
	public PoProcessDTO(@NonNull PoTransactionProcess<?> process) {
		super(process);
		this.poCode = new PoCodeDTO(process.getPoCode());
	}
	
}