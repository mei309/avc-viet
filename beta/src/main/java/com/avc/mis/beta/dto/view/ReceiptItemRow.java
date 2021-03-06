/**
 * 
 */
package com.avc.mis.beta.dto.view;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.avc.mis.beta.dto.BasicDTO;
import com.avc.mis.beta.dto.basic.ItemWithUnitDTO;
import com.avc.mis.beta.dto.basic.PoCodeBasic;
import com.avc.mis.beta.entities.embeddable.AmountWithUnit;
import com.avc.mis.beta.entities.enums.ItemGroup;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.enums.ProcessStatus;
import com.avc.mis.beta.entities.values.Item;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.NonFinal;

/**
 * Row in list of receipt items report.
 * 
 * @author Zvi
 *
 */
@Value
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
public class ReceiptItemRow extends BasicDTO {
	
	@Getter(value = AccessLevel.NONE)
	private List<MeasureUnit> displayMeasureUnits = Arrays.asList(MeasureUnit.KG, MeasureUnit.LBS);

	PoCodeBasic poCode;
	String supplierName;
	ItemWithUnitDTO item;
	AmountWithUnit receivedOrderUnits;
	@JsonIgnore
	AmountWithUnit receiptAmt;
	AmountWithUnit orderAmount;
	@NonFinal AmountWithUnit orderBalance;
	@JsonIgnore
	LocalDateTime receiptDate;
	@JsonIgnore
	ProcessStatus status;
	String storage;
	AmountWithUnit extraAdded;
	
	public ReceiptItemRow(@NonNull Integer id, 
			Integer poCodeId, String poCodeCode, String contractTypeCode, String contractTypeSuffix, String supplierName, 
			Integer itemId, String itemValue, MeasureUnit itemeasureUnit, ItemGroup itemGroup, 
			AmountWithUnit unit, Class<? extends Item> clazz,
			BigDecimal orderAmount, MeasureUnit orderMU, 
			BigDecimal receivedOrderAmount, MeasureUnit receivedOrderMU, 			
			LocalDateTime receiptDate, ProcessStatus status,
			BigDecimal receiptAmount, MeasureUnit receiptMU, String storage, 
			BigDecimal extraAdded, MeasureUnit extraAddedMU) {
		super(id);
		this.poCode = new PoCodeBasic(poCodeId, poCodeCode, contractTypeCode, contractTypeSuffix, supplierName);
		this.supplierName = supplierName;
		this.item = new ItemWithUnitDTO(itemId, itemValue, itemeasureUnit, itemGroup, null, unit, clazz);

		this.receivedOrderUnits = new AmountWithUnit(receivedOrderAmount, receivedOrderMU);
		this.receiptAmt = new AmountWithUnit(receiptAmount, receiptMU);
		try {
			this.orderBalance = this.receiptAmt.subtract(this.receivedOrderUnits);
		} catch (UnsupportedOperationException e) {}
		if(orderAmount != null) {
			this.orderAmount = new AmountWithUnit(orderAmount, orderMU);
		}
		else {
			this.orderAmount = null;
		}
		this.receiptDate = receiptDate;
		this.status = status;
				
		this.storage = storage;
		if(extraAdded != null) {
			this.extraAdded = new AmountWithUnit(extraAdded, extraAddedMU);
		}
		else {
			this.extraAdded = null;
		}
	}
	
	public List<AmountWithUnit> getReceiptAmount() {
		return AmountWithUnit.amountDisplay(this.receiptAmt, this.item, displayMeasureUnits);
	}
	
}
