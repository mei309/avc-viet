/**
 * 
 */
package com.avc.mis.beta.dto.view;

import java.util.List;

import com.avc.mis.beta.dto.ValueDTO;
import com.avc.mis.beta.dto.values.ItemDTO;
import com.avc.mis.beta.entities.embeddable.AmountWithUnit;
import com.avc.mis.beta.entities.enums.MeasureUnit;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

/**
 * DTO of inventory for one item. 
 * Contains total stock in inventory for this item, it's storages
 * and information of the originating process, process item and used amounts.
 * 
 * @author Zvi
 *
 */
@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ItemInventoryRow extends ValueDTO {
	
	ItemDTO item;
	AmountWithUnit[] totalStock;
	
	List<ProcessItemInventoryRow> poInventoryRows;

	public ItemInventoryRow(@NonNull ItemDTO item, @NonNull AmountWithUnit totalStock,
			List<ProcessItemInventoryRow> poInventoryRows) {
		super(item.getId());
		this.item = item;
		this.totalStock = new AmountWithUnit[2];
		this.totalStock[0] = totalStock.setScale(MeasureUnit.SCALE);
		this.totalStock[1] = totalStock.convert(MeasureUnit.LOT).setScale(MeasureUnit.SCALE);
		this.poInventoryRows = poInventoryRows;
	}
	

	
}
