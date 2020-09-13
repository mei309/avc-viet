package com.avc.mis.beta.dto.processinfo;

import java.math.BigDecimal;

import com.avc.mis.beta.dto.ProcessDTO;
import com.avc.mis.beta.dto.process.PoCodeDTO;
import com.avc.mis.beta.dto.values.BasicValueEntity;
import com.avc.mis.beta.entities.embeddable.AmountWithUnit;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.processinfo.ProcessItem;
import com.avc.mis.beta.entities.processinfo.Storage;
import com.avc.mis.beta.entities.processinfo.UsedItem;
import com.avc.mis.beta.entities.values.Item;
import com.avc.mis.beta.entities.values.Warehouse;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UsedItemDTO extends ProcessDTO {

	private BasicValueEntity<Item> item;
	private PoCodeDTO itemPo;
	private BigDecimal numberUnits;

	private StorageDTO storage;

	private Integer ordinal;	
	private AmountWithUnit unitAmount;
	private BasicValueEntity<Warehouse> warehouseLocation;
	private BigDecimal containerWeight;	
	
	private Warehouse newLocation;
	
	public UsedItemDTO(Integer id, Integer version, BigDecimal numberUnits,
			Integer itemId, String itemValue, 
			Integer poCodeId, String contractTypeCode, String contractTypeSuffix, String supplierName,
			Integer storageId, Integer stoageVersion, Integer storageOrdinal,
			BigDecimal unitAmount, MeasureUnit measureUnit, BigDecimal storageNumberUnits, BigDecimal containerWeight,
			Integer warehouseLocationId,  String warehouseLocationValue, String storageRemarks) {
		super(id, version);
		this.numberUnits = numberUnits;
		this.item = new BasicValueEntity<Item>(itemId, itemValue);
		if(poCodeId != null)
			this.itemPo = new PoCodeDTO(poCodeId, contractTypeCode, contractTypeSuffix, supplierName);
		else
			this.itemPo = null;
		this.storage = new StorageDTO(storageId, stoageVersion, storageOrdinal, 
				unitAmount, measureUnit, storageNumberUnits, containerWeight, warehouseLocationId, warehouseLocationValue, 
				storageRemarks, null);

		this.ordinal = storageOrdinal;
		this.unitAmount = new AmountWithUnit(unitAmount.setScale(MeasureUnit.SCALE), measureUnit);
		if(warehouseLocationId != null && warehouseLocationValue != null)
			this.warehouseLocation = new BasicValueEntity<Warehouse>(warehouseLocationId,  warehouseLocationValue);
		else
			this.warehouseLocation = null;
		this.containerWeight = containerWeight;
	
	}

	public UsedItemDTO(UsedItem usedItem) {
		super(usedItem.getId(), usedItem.getVersion());
		this.numberUnits = usedItem.getNumberUnits();
		Storage storage = usedItem.getStorage();
		ProcessItem processItem = storage.getProcessItem();
		this.item = new BasicValueEntity<Item>(processItem.getItem());
		this.itemPo = new PoCodeDTO((processItem.getProcess()).getPoCode());
		this.storage = new StorageDTO(storage);

		this.ordinal = storage.getOrdinal();
		this.unitAmount = storage.getUnitAmount().setScale(MeasureUnit.SCALE);
		if(storage.getWarehouseLocation() != null) {
			this.warehouseLocation = new BasicValueEntity<Warehouse>(
					storage.getWarehouseLocation().getId(),  storage.getWarehouseLocation().getValue());
		}
		else {
			this.warehouseLocation = null;
		}
		this.containerWeight = storage.getContainerWeight();

	}

	
}
