/**
 * 
 */
package com.avc.mis.beta.dto.process;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

import org.springframework.data.jpa.repository.Query;

import com.avc.mis.beta.dto.ProcessDTO;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.process.PoCode;
import com.avc.mis.beta.entities.process.ProcessItem;
import com.avc.mis.beta.entities.values.Item;
import com.avc.mis.beta.entities.values.Storage;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * @author Zvi
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProcessItemDTO extends ProcessDTO {

	Item item;
	PoCode itemPo;
	BigDecimal unitAmount;
	MeasureUnit measureUnit;
	BigDecimal numberUnits;	
	Storage storageLocation;
	String description;
	String remarks;
	
	public ProcessItemDTO(Integer id, Integer version, Item item, PoCode itemPo,
			BigDecimal unitAmount, MeasureUnit measureUnit, BigDecimal numberUnits, 
			Storage storageLocation, String description, String remarks) {
		super(id, version);
		this.item = item;
		this.itemPo = itemPo;
		this.unitAmount = unitAmount.setScale(3);
		this.measureUnit = measureUnit;
		this.numberUnits = numberUnits.setScale(3);
		this.storageLocation = storageLocation;
		this.description = description;
		this.remarks = description;
		
//		this.unitAmount.setScale(3);//for testing with assertEquals
//		this.numberUnits.setScale(3);//for testing with assertEquals
		
	}
	
	
	/**
	 * @param processItem
	 */
	public ProcessItemDTO(ProcessItem processItem) {
		super(processItem.getId(), processItem.getVersion());
		this.description = processItem.getDescription();
		this.item = processItem.getItem();
		this.itemPo = processItem.getItemPo();
		this.measureUnit = processItem.getMeasureUnit();
		this.unitAmount = processItem.getUnitAmount().setScale(3);
		this.numberUnits = processItem.getNumberUnits().setScale(3);
		this.storageLocation = processItem.getStorageLocation();
		this.remarks = processItem.getRemarks();
		
//		this.unitAmount.setScale(3);//for testing with assertEquals
//		this.numberUnits.setScale(3);//for testing with assertEquals
		
	}


	

	
}
