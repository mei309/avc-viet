/**
 * 
 */
package com.avc.mis.beta.dto.view;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.stream.Stream;

import com.avc.mis.beta.dto.BasicDTO;
import com.avc.mis.beta.dto.values.BasicValueEntity;
import com.avc.mis.beta.dto.values.PoCodeBasic;
import com.avc.mis.beta.entities.embeddable.AmountWithCurrency;
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
 * DTO used for report/table to represent essential order-item information,
 * including PO information of the order, to be represented or aggregated in the table.
 * 
 * @author Zvi
 *
 */
@Value
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
public class PoItemRow extends BasicDTO {
	
	String personInCharge;
	PoCodeBasic poCode;
	String[] approvals;
	String supplierName;
	BasicValueEntity<Item> item;
//	String itemName;
	@JsonIgnore
	AmountWithUnit numUnits;
	OffsetDateTime contractDate;
	LocalDate deliveryDate;
	String defects;
	AmountWithCurrency unitPrice;
	BigDecimal receivedOrderUnits;
	BigDecimal receivedAmount;
	List<String> orderStatus;
	
	/**
	 * All arguments Constructor ,
	 * used to project directly from database without nested fetching.
	 */
	public PoItemRow(@NonNull Integer id, String personInCharge,
			Integer poCodeId, String poCodeCode, String contractTypeCode, String contractTypeSuffix, String supplierName, 
			String approvals,
			Integer itemId, String itemValue, BigDecimal amount, MeasureUnit measureUnit, 
			OffsetDateTime contractDate, LocalDate deliveryDate, 
			String defects, BigDecimal unitPrice, Currency currency, 
			BigDecimal receivedOrderUnits,
			BigDecimal receivedAmount, ProcessStatus status, long receiptsCancelled) {
		super(id);
		this.personInCharge = personInCharge;
		this.poCode = new PoCodeBasic(poCodeId, poCodeCode, contractTypeCode, contractTypeSuffix, supplierName);
		if(approvals != null) {
			this.approvals = Stream.of(approvals.split(",")).distinct().toArray(String[]::new);
		}
		else {
			this.approvals = null;
		}
		this.supplierName = supplierName;
		this.item = new BasicValueEntity<Item>(itemId, itemValue);
//		this.itemName = itemName;
		this.numUnits = new AmountWithUnit(amount, measureUnit);
//		this.numberUnits = new AmountWithUnit[] {
//				numberUnits.setScale(MeasureUnit.SCALE), 
//				numberUnits.convert(MeasureUnit.LOT).setScale(MeasureUnit.SCALE)};
		this.contractDate = contractDate;
		this.deliveryDate = deliveryDate;
		this.defects = defects;
		if(unitPrice != null) {
			this.unitPrice = new AmountWithCurrency(unitPrice, currency);
		}
		else {
			this.unitPrice = null;
		}
		this.receivedOrderUnits = receivedOrderUnits;
		this.receivedAmount = receivedAmount;
		
		this.orderStatus = new ArrayList<String>();
		
		if(status == ProcessStatus.CANCELLED) {
			this.orderStatus.add("CANCELLED");
		}
		else if(receivedOrderUnits == null) {
			this.orderStatus.add("OPEN");
		}
		else {
			switch(receivedOrderUnits.compareTo(numUnits.getAmount())) {
			case -1:
				this.orderStatus.add("OPEN");
				if(receivedOrderUnits.signum() > 0) {
					this.orderStatus.add("PARTLY RECEIVED");
				}
				break;
			case 0:
			case 1:
				this.orderStatus.add("RECEIVED");
				break;
			}
		}
		
		if(receiptsCancelled > 0) {
			this.orderStatus.add("REJECTED");
		}
	}
	
	public AmountWithUnit[] getNumberUnits() {
		if(MeasureUnit.WEIGHT_UNITS.contains(this.numUnits.getMeasureUnit())) {
			return new AmountWithUnit[] {
					this.numUnits.setScale(MeasureUnit.SCALE),
					this.numUnits.convert(MeasureUnit.LOT).setScale(MeasureUnit.SCALE)};
		}
		return null;
	}
		
	/**
	 * @return a string representing full PO code. e.g. VAT-900001, PO-900002V
	 */
	public String getValue() {
		return this.poCode.getValue();
	}
	
	
}
