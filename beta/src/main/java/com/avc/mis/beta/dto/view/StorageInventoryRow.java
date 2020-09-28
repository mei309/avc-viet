/**
 * 
 */
package com.avc.mis.beta.dto.view;

import java.math.BigDecimal;

import com.avc.mis.beta.dto.SubjectDataDTO;
import com.avc.mis.beta.dto.values.BasicValueEntity;
import com.avc.mis.beta.entities.embeddable.AmountWithUnit;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.values.Warehouse;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

/**
 * DTO used as the list of storages for ProcessItemInventoryRow.
 * Contains id (for reference) of process item and storage amount, location and amounts used.
 * 
 * @author Zvi
 *
 */
@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StorageInventoryRow  extends SubjectDataDTO {

	Integer processItemId;
	AmountWithUnit unitAmount;
	BigDecimal numberUnits;	
	BigDecimal containerWeight;
	BasicValueEntity<Warehouse> warehouseLocation;
	BigDecimal numberUsedUnits;
	AmountWithUnit totalBalance;
	
	/**
	 * All database fields (the fields in the form they are fetched from the db) arguments constructor.
	 */
	public StorageInventoryRow(Integer id, Integer version, Integer ordinal,
			Integer processItemId,
			BigDecimal unitAmount, MeasureUnit measureUnit, BigDecimal numberUnits, BigDecimal containerWeight,
			Integer warehouseLocationId,  String warehouseLocationValue,
			BigDecimal numberUsedUnits, 
			BigDecimal totalBalance, MeasureUnit totalBalanceMU) {
		super(id, version, ordinal);
		this.processItemId = processItemId;
		this.unitAmount = new AmountWithUnit(unitAmount, measureUnit);;
		this.numberUnits = numberUnits;
		this.containerWeight = containerWeight;
		if(warehouseLocationId != null && warehouseLocationValue != null)
			this.warehouseLocation = new BasicValueEntity<Warehouse>(warehouseLocationId,  warehouseLocationValue);
		else
			this.warehouseLocation = null;
		this.numberUsedUnits = numberUsedUnits;
		this.totalBalance = new AmountWithUnit(totalBalance, totalBalanceMU);
	}
	
	/**
	 * All class arguments constructor
	 */
	public StorageInventoryRow(Integer id, Integer version, Integer ordinal,
			Integer processItemId, AmountWithUnit unitAmount, BigDecimal containerWeight,
			BigDecimal numberUnits, BasicValueEntity<Warehouse> warehouseLocation, 
			BigDecimal numberUsedUnits, AmountWithUnit totalBalance) {
		super(id, version, ordinal);
		this.processItemId = processItemId;
		this.unitAmount = unitAmount;
		this.numberUnits = numberUnits;
		this.containerWeight = containerWeight;
		this.warehouseLocation = warehouseLocation;
		this.numberUsedUnits = numberUsedUnits;
		this.totalBalance = totalBalance;
	}
}
