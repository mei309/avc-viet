/**
 * 
 */
package com.avc.mis.beta.dto.queryRows;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;

import com.avc.mis.beta.dto.ValueDTO;
import com.avc.mis.beta.dto.process.PoCodeDTO;
import com.avc.mis.beta.dto.values.BasicValueEntity;
import com.avc.mis.beta.entities.embeddable.AmountWithUnit;
import com.avc.mis.beta.entities.enums.ContractTypeCode;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.values.Item;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Zvi
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProcessItemInventoryRow extends ValueDTO {

//	ItemDTO item; //measure unit is already in totalAmount
	private BasicValueEntity<Item> item;
	private PoCodeDTO poCode;
	private OffsetDateTime receiptDate;
	private AmountWithUnit totalAmount;
	private AmountWithUnit totalLots; //total amount in lots
	
	private List<StorageInventoryRow> storageForms;
	
	public ProcessItemInventoryRow(Integer id, Integer itemId, String itemValue,
			Integer poCodeId, ContractTypeCode contractTypeCode, String supplierName,
			OffsetDateTime receiptDate/* , BigDecimal totalAmount, MeasureUnit measureUnit */) {
		super(id);
		this.item = new BasicValueEntity<Item>(itemId, itemValue);
		this.poCode = new PoCodeDTO(poCodeId, contractTypeCode, supplierName);
		this.receiptDate = receiptDate;
//		this.totalAmount = new AmountWithUnit(
//				totalAmount.setScale(AmountWithUnit.SCALE, RoundingMode.HALF_DOWN), measureUnit);
				
	}
	
	public void setStorageForms(List<StorageInventoryRow> storageForms) {
		if(storageForms.size() > 0) {
			MeasureUnit measureUnit = storageForms.get(0).getUnitAmount().getMeasureUnit();
			this.totalAmount = storageForms.stream()
					.map(StorageInventoryRow::getTotalBalance)
					.reduce(new AmountWithUnit(BigDecimal.ZERO, measureUnit), AmountWithUnit::add);
			this.totalLots = new AmountWithUnit(
					MeasureUnit.convert(totalAmount, MeasureUnit.LOT)
					.setScale(AmountWithUnit.SCALE, RoundingMode.HALF_DOWN), MeasureUnit.LOT);

		}
		this.storageForms = storageForms;
		
	}
	
}
