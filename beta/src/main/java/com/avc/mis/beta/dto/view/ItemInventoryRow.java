/**
 * 
 */
package com.avc.mis.beta.dto.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.avc.mis.beta.dto.BasicDTO;
import com.avc.mis.beta.dto.basic.ItemWithUnitDTO;
import com.avc.mis.beta.entities.embeddable.AmountWithUnit;
import com.avc.mis.beta.entities.enums.ItemGroup;
import com.avc.mis.beta.entities.enums.MeasureUnit;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * DTO of inventory for one item. 
 * Contains total stock in inventory for this item, it's storages
 * and information of the originating process, process item and used amounts.
 * 
 * @author Zvi
 *
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
public class ItemInventoryRow extends BasicDTO {
	
	@Getter(value = AccessLevel.NONE)
	private List<MeasureUnit> displayMeasureUnits = Arrays.asList(MeasureUnit.LBS, MeasureUnit.LOT);
	
	private ItemWithUnitDTO item;
	
	private AmountWithUnit totalAmount;
	private AmountWithUnit totalWeight;
	private List<ProcessItemInventoryRow> poInventoryRows;

	public ItemInventoryRow(@NonNull ItemWithUnitDTO item) {
		super(item.getId());
		this.item = item;
	}
	
	public void setPoInventoryRows(List<ProcessItemInventoryRow> poInventoryRows) {
		this.poInventoryRows = poInventoryRows;
		this.totalWeight = ProcessItemInventoryRow.getTotalWeight(poInventoryRows);
		
		if(MeasureUnit.NONE == item.getUnit().getMeasureUnit() && MeasureUnit.WEIGHT_UNITS.contains(item.getMeasureUnit())) {
			this.totalAmount = null;
		}
		else {
			this.totalAmount = ProcessItemInventoryRow.getTotalAmount(poInventoryRows);
		}
	}
	
	public List<AmountWithUnit> getTotalStock() {
		List<AmountWithUnit> totalStock = new ArrayList<>();
		if(this.totalAmount != null) {
			totalStock.add(this.totalAmount);
		}
		if(this.totalWeight != null) {
			if(item.getGroup() == ItemGroup.PRODUCT) {
				totalStock.addAll(AmountWithUnit.weightDisplay(this.totalWeight, displayMeasureUnits));
			}
			else {
				totalStock.add(this.totalWeight.setScale(MeasureUnit.SCALE));
			}
		}
		return totalStock;
	}
	
}
