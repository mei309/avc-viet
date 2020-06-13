/**
 * 
 */
package com.avc.mis.beta.dto.values;

import java.math.BigDecimal;

import com.avc.mis.beta.dto.process.RawItemQualityDTO;
import com.avc.mis.beta.dto.process.StorageDTO;
import com.avc.mis.beta.entities.enums.CheckStatus;
import com.avc.mis.beta.entities.enums.ContractTypeCode;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.process.Storage;

import lombok.Data;

/**
 * @author Zvi
 *
 */
@Data
public class RawItemQualityWithStorage {
	
	private RawItemQualityDTO rawItemQuality;
	private StorageDTO storage;

	public RawItemQualityWithStorage(Integer id, Integer version, 
			Integer itemId, String itemValue, 
			Integer poCodeId, ContractTypeCode contractTypeCode, String supplierName, 
			Integer storageId, Integer storageVersion,
			BigDecimal unitAmount, MeasureUnit measureUnit, BigDecimal numberUnits, 
			Integer warehouseLocationId,  String warehouseLocationValue, String storageRemarks,
			String description, String remarks, Class<? extends Storage> clazz,
			BigDecimal breakage, BigDecimal foreignMaterial, BigDecimal humidity, BigDecimal testa,
			BigDecimal scorched, BigDecimal deepCut, BigDecimal offColour, BigDecimal shrivel, BigDecimal desert,
			BigDecimal deepSpot, BigDecimal mold, BigDecimal dirty, BigDecimal decay, BigDecimal insectDamage,
			BigDecimal count, BigDecimal smallKernels, BigDecimal defectsAfterRoasting, BigDecimal weightLoss,
			CheckStatus colour, CheckStatus flavour) {
		this.rawItemQuality = new RawItemQualityDTO(id, version, 
				itemId, itemValue, 
				poCodeId, contractTypeCode, supplierName, 
				description, remarks, 
				breakage, foreignMaterial, humidity, testa,
				scorched, deepCut, offColour, shrivel, desert,
				deepSpot, mold, dirty, decay, insectDamage,
				count, smallKernels, defectsAfterRoasting, weightLoss,
				colour, flavour);
		this.storage = new StorageDTO(storageId, storageVersion, 
				unitAmount, measureUnit, numberUnits, 
				warehouseLocationId, warehouseLocationValue, storageRemarks, clazz);
	}
	
	
	
	/**
	 * @return id of RawItemQuality. 
	 * Used for mapping to the logical structure that every RawItemQuality has a collection of storages.
	 */
	public Integer getId() {
		return rawItemQuality.getId();
	}
	
}