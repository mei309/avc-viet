/**
 * 
 */
package com.avc.mis.beta.dto.process;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.avc.mis.beta.dto.processinfo.ReceiptItemDTO;
import com.avc.mis.beta.dto.processinfo.StorageBaseDTO;
import com.avc.mis.beta.dto.query.ReceiptItemWithStorage;
import com.avc.mis.beta.entities.Ordinal;
import com.avc.mis.beta.entities.enums.EditStatus;
import com.avc.mis.beta.entities.enums.ProcessName;
import com.avc.mis.beta.entities.enums.ProcessStatus;
import com.avc.mis.beta.entities.process.Receipt;
import com.avc.mis.beta.entities.processinfo.ReceiptItem;
import com.avc.mis.beta.entities.values.ProductionLine;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

/**
 * DTO(Data Access Object) for sending or displaying Receipt entity data.
 * 
 * @author Zvi
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class ReceiptDTO extends PoProcessDTO {

	private List<ReceiptItemDTO> receiptItems;
	
	public ReceiptDTO(Integer id, Integer version, Instant createdDate, String userRecording, 
			Integer poCodeId, String contractTypeCode, String contractTypeSuffix, 
			Integer supplierId, Integer supplierVersion, String supplierName,  
			ProcessName processName, ProductionLine productionLine, 
			OffsetDateTime recordedTime, LocalTime startTime, LocalTime endTime, Duration duration,
			Integer numOfWorkers, ProcessStatus processStatus, EditStatus editStatus, String remarks, String approvals) {
		super(id, version, createdDate, userRecording, 
				poCodeId, contractTypeCode, contractTypeSuffix,
				supplierId, supplierVersion, supplierName, 
				processName, productionLine, recordedTime, startTime, endTime, duration,
				numOfWorkers, processStatus, editStatus, remarks, approvals);
	}

	public ReceiptDTO(@NonNull Receipt receipt) {
		super(receipt);
		this.receiptItems = Arrays.stream(receipt.getReceiptItems())
				.map(i->{return new ReceiptItemDTO((ReceiptItem) i);}).collect(Collectors.toList());

	}
	
	/**
	 * Used for setting receiptItems from a flat form produced by a join of receipt items and it's storage info, 
	 * to receiptItems that each have a Set of storages.
	 * @param receiptItems collection of ReceiptItemWithStorage that contain all receipt items with storage detail.
	 */
	public void setReceiptItems(List<ReceiptItemWithStorage> receiptItems) {
		Map<Integer, List<ReceiptItemWithStorage>> map = receiptItems.stream()
			.collect(Collectors.groupingBy(ReceiptItemWithStorage::getId, Collectors.toList()));
		this.receiptItems = new ArrayList<ReceiptItemDTO>();
		for(List<ReceiptItemWithStorage> list: map.values()) {
			ReceiptItemDTO receiptItem = list.get(0).getReceiptItem();
			//group list to storage/extraAdded and set accordingly
			receiptItem.setStorageForms(list.stream()
					.map(i -> i.getStorage())
					.sorted(Ordinal.ordinalComparator())
					.collect(Collectors.toList()));
			this.receiptItems.add(receiptItem);
		}
		this.receiptItems.sort(Ordinal.ordinalComparator());
	}	

	@Override
	public String getProcessTypeDescription() {
		return "Receipt";
	}

}
