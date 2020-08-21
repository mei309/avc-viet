/**
 * 
 */
package com.avc.mis.beta.dto.query;

import java.math.BigDecimal;

import com.avc.mis.beta.dto.ValueDTO;
import com.avc.mis.beta.dto.process.PoCodeDTO;
import com.avc.mis.beta.dto.processinfo.LoadedItemDTO;
import com.avc.mis.beta.dto.processinfo.ProcessItemDTO;
import com.avc.mis.beta.dto.processinfo.StorageDTO;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.processinfo.Storage;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zvi
 *
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class LoadedItemWithStorage extends ValueDTO {

	private LoadedItemDTO loadedItem;
	private PoCodeDTO po;
	private StorageDTO storage;
	
	public LoadedItemWithStorage(Integer id, Integer version, Integer itemId, String itemValue, Integer poCodeId,
			String contractTypeCode, String contractTypeSuffix, String supplierName, Integer storageId,
			Integer storageVersion, String storageName, BigDecimal unitAmount, MeasureUnit measureUnit,
			BigDecimal numberUnits, BigDecimal containerWeight, Integer warehouseLocationId,
			String warehouseLocationValue, String storageRemarks, Class<? extends Storage> clazz, String description,
			String remarks,
			Integer itemPoCodeId, String itemContractTypeCode, String itemContractTypeSuffix, String itemSupplierName) {
		super(id);
		this.loadedItem = new LoadedItemDTO(id, version, itemId, itemValue, 
				description, remarks,
				itemPoCodeId, itemContractTypeCode, itemContractTypeSuffix, itemSupplierName);
		this.po = new PoCodeDTO(poCodeId, contractTypeCode, contractTypeSuffix, supplierName);
		this.storage = new StorageDTO(storageId, storageVersion, storageName,
				unitAmount, measureUnit, numberUnits, containerWeight,
				warehouseLocationId, warehouseLocationValue, storageRemarks, clazz);
	}

}