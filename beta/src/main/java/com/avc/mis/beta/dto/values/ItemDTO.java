/**
 * 
 */
package com.avc.mis.beta.dto.values;

import com.avc.mis.beta.dto.ValueDTO;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.item.Item;
import com.avc.mis.beta.entities.item.ItemGroup;
import com.avc.mis.beta.entities.item.ProductionUse;

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
public class ItemDTO extends ValueDTO {

	String value;
	MeasureUnit defaultMeasureUnit;
//	String dtype;
	ItemGroup group;
	ProductionUse productionUse;

	
	public ItemDTO(Integer id, String value, MeasureUnit measureUnit, 
			ItemGroup group, ProductionUse productionUse) {
		super(id);
		this.value = value;
		this.defaultMeasureUnit = measureUnit;
		this.group = group;
		this.productionUse = productionUse;
	}
	
	public ItemDTO(@NonNull Item item) {
		super(item.getId());
		this.value = item.getValue();
		this.defaultMeasureUnit = item.getDefaultMeasureUnit();
		this.group = item.getItemGroup();
		this.productionUse = item.getProductionUse();
	}

}
