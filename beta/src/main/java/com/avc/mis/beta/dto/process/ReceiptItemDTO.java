/**
 * 
 */
package com.avc.mis.beta.dto.process;

import java.math.BigDecimal;

import com.avc.mis.beta.dto.values.ObjectWithIdAndVersion;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.process.PoCode;
import com.avc.mis.beta.entities.process.ReceiptItem;
import com.avc.mis.beta.entities.values.Item;
import com.avc.mis.beta.entities.values.Storage;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Zvi
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReceiptItemDTO extends ProcessItemDTO {
	
	private ObjectWithIdAndVersion orderItemId;

	public ReceiptItemDTO(Integer id, Integer version, Item item, PoCode itemPo, BigDecimal unitAmount,
			MeasureUnit measureUnit, BigDecimal numberUnits, Storage storageLocation,
			String description, String remarks, Integer orderItemId, Integer orderItemVersion) {
		super(id, version, item, itemPo, unitAmount, measureUnit, numberUnits, storageLocation, description,
				remarks);
		this.orderItemId = new ObjectWithIdAndVersion(orderItemId, orderItemVersion);
	}

	
	public ReceiptItemDTO(ReceiptItem receiptItem) {
		super(receiptItem);
		if(receiptItem.getOrderItem() != null)
			this.orderItemId = new ObjectWithIdAndVersion(receiptItem.getOrderItem());
	}

}
