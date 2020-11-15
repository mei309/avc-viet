/**
 * 
 */
package com.avc.mis.beta.dto.view;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.avc.mis.beta.dto.ValueDTO;
import com.avc.mis.beta.dto.values.BasicValueEntity;
import com.avc.mis.beta.dto.values.PoCodeBasic;
import com.avc.mis.beta.entities.embeddable.AmountWithUnit;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.item.Item;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

/**
 * @author Zvi
 *
 */
@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ReceiptItemRow extends ValueDTO {
	
	PoCodeBasic poCode;
	String supplierName;
	BasicValueEntity<Item> item;
//	String itemName;
	AmountWithUnit orderAmount;
	AmountWithUnit orderBalance;
	OffsetDateTime receiptDate;
	AmountWithUnit receiptAmount[];
	String storage;
	AmountWithUnit extraAdded;
	
	public ReceiptItemRow(@NonNull Integer id, 
			Integer poCodeId, String contractTypeCode, String contractTypeSuffix, String supplierName, 
			Integer itemId, String itemValue,
			BigDecimal orderAmount, MeasureUnit orderMU, OffsetDateTime receiptDate, 
			BigDecimal receiptAmount, MeasureUnit receiptMU, String storage, 
			BigDecimal extraAdded, MeasureUnit extraAddedMU) {
		super(id);
		this.poCode = new PoCodeBasic(poCodeId, contractTypeCode, contractTypeSuffix);
		this.supplierName = supplierName;
		this.item = new BasicValueEntity<Item>(itemId, itemValue);
//		this.itemName = itemName;

		AmountWithUnit receiptAmt = new AmountWithUnit(receiptAmount, receiptMU);
		if(orderAmount != null) {
			this.orderAmount = new AmountWithUnit(orderAmount, orderMU);
			this.orderBalance = receiptAmt.substract(this.orderAmount);
		}
		else {
			this.orderAmount = null;
			this.orderBalance = null;
		}
		this.receiptDate = receiptDate;
		
		
		this.receiptAmount = new AmountWithUnit[] {
				receiptAmt,
				receiptAmt.convert(MeasureUnit.LBS)
		};
		
		this.storage = storage;
		if(extraAdded != null) {
			this.extraAdded = new AmountWithUnit(extraAdded, extraAddedMU);
		}
		else {
			this.extraAdded = null;
		}
	}
	
}
