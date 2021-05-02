/**
 * 
 */
package com.avc.mis.beta.dto.exportdoc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.avc.mis.beta.dto.BasicDTO;
import com.avc.mis.beta.dto.values.BasicValueEntity;
import com.avc.mis.beta.entities.embeddable.AmountWithUnit;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.item.BulkItem;
import com.avc.mis.beta.entities.item.Item;
import com.avc.mis.beta.entities.item.PackedItem;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

/**
 * @author zvi
 *
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class ContainerPoItemRow extends BasicDTO {

	BasicValueEntity<Item> item;
	String[] poCodes;

	AmountWithUnit total;

	public ContainerPoItemRow(@NonNull Integer id, 
			Integer itemId, String itemValue, MeasureUnit defaultMeasureUnit, 
			BigDecimal itemUnitAmount, MeasureUnit itemMeasureUnit, Class<? extends Item> itemClazz, 
			String poCodes,
			BigDecimal total, MeasureUnit measureUnit) {
		super(id);
		this.item = new BasicValueEntity<Item>(itemId, itemValue);
		if(poCodes != null)
			this.poCodes = Stream.of(poCodes.split(",")).toArray(String[]::new);
		else
			this.poCodes = null;
		if(itemClazz == BulkItem.class) {
			this.total = new AmountWithUnit(total, measureUnit);
		}
		else if(itemClazz == PackedItem.class){
			this.total = new AmountWithUnit(total.multiply(itemUnitAmount), itemMeasureUnit);
		}
		else 
		{
			throw new IllegalStateException("The class can only apply to weight items");
		}
		
	}
	
	public List<AmountWithUnit> getTotalRow() {
		return AmountWithUnit.weightDisplay(this.total, Arrays.asList(MeasureUnit.LBS, MeasureUnit.KG));
	}

}