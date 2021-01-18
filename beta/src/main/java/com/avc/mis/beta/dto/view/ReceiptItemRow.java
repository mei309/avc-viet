/**
 * 
 */
package com.avc.mis.beta.dto.view;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.avc.mis.beta.dto.BasicDTO;
import com.avc.mis.beta.dto.basic.PoCodeBasic;
import com.avc.mis.beta.dto.values.BasicValueEntity;
import com.avc.mis.beta.entities.embeddable.AmountWithUnit;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.enums.ProcessStatus;
import com.avc.mis.beta.entities.item.Item;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

/**
 * @author Zvi
 *
 */
@Value
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
public class ReceiptItemRow extends BasicDTO {
	
	PoCodeBasic poCode;
	String supplierName;
	BasicValueEntity<Item> item;
//	String itemName;
	AmountWithUnit receivedOrderUnits;
	@JsonIgnore
	AmountWithUnit receiptAmt;
	AmountWithUnit orderAmount;
	AmountWithUnit orderBalance;
	@JsonIgnore
	OffsetDateTime receiptDate;
	@JsonIgnore
	ProcessStatus status;
	String storage;
	AmountWithUnit extraAdded;
	
	public ReceiptItemRow(@NonNull Integer id, 
			Integer poCodeId, String poCodeCode, String contractTypeCode, String contractTypeSuffix, String supplierName, 
			Integer itemId, String itemValue,
			BigDecimal orderAmount, MeasureUnit orderMU, 
			BigDecimal receivedOrderAmount, MeasureUnit receivedOrderMU, 			
			OffsetDateTime receiptDate, ProcessStatus status,
			BigDecimal receiptAmount, MeasureUnit receiptMU, String storage, 
			BigDecimal extraAdded, MeasureUnit extraAddedMU) {
		super(id);
		this.poCode = new PoCodeBasic(poCodeId, poCodeCode, contractTypeCode, contractTypeSuffix);
		this.supplierName = supplierName;
		this.item = new BasicValueEntity<Item>(itemId, itemValue);
//		this.itemName = itemName;

		this.receivedOrderUnits = new AmountWithUnit(receivedOrderAmount, receivedOrderMU);
		this.receiptAmt = new AmountWithUnit(receiptAmount, receiptMU);
		this.orderBalance = this.receiptAmt.subtract(this.receivedOrderUnits);
		if(orderAmount != null) {
			this.orderAmount = new AmountWithUnit(orderAmount, orderMU);
//			this.orderBalance = this.receiptAmt.subtract(this.orderAmount);
		}
		else {
			this.orderAmount = null;
//			this.orderBalance = null;
		}
		this.receiptDate = receiptDate;
		this.status = status;
		
//		this.receiptAmount = new AmountWithUnit[] {
//				receiptAmt,
//				receiptAmt.convert(MeasureUnit.LBS)
//		};
		
		this.storage = storage;
		if(extraAdded != null) {
			this.extraAdded = new AmountWithUnit(extraAdded, extraAddedMU);
		}
		else {
			this.extraAdded = null;
		}
	}
	
	public AmountWithUnit[] getReceiptAmount() {
		if(MeasureUnit.WEIGHT_UNITS.contains(this.receiptAmt.getMeasureUnit())) {
			return new AmountWithUnit[] {
					this.receiptAmt.setScale(MeasureUnit.SCALE),
					this.receiptAmt.convert(MeasureUnit.LOT).setScale(MeasureUnit.SCALE)};
		}
		return null;
	}
	
}
