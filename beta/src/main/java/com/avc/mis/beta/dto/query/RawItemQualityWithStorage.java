/**
 * 
 */
package com.avc.mis.beta.dto.query;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.avc.mis.beta.dto.processinfo.RawItemQualityDTO;
import com.avc.mis.beta.dto.processinfo.StorageDTO;
import com.avc.mis.beta.entities.enums.CheckStatus;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.processinfo.Storage;

import lombok.Data;

/**
 * @author Zvi
 *
 */
@Data
@Deprecated
public class RawItemQualityWithStorage {
	
	private RawItemQualityDTO rawItemQuality;
	private StorageDTO storage;

	public RawItemQualityWithStorage(Integer id, Integer version, 
			Integer itemId, String itemValue, 
			MeasureUnit testMU, BigDecimal sampleWeight, BigInteger numberOfSamples,
			/* Integer poCodeId, ContractTypeCode contractTypeCode, String supplierName, */
			Integer storageId, Integer storageVersion,
			BigDecimal unitAmount, MeasureUnit measureUnit, BigDecimal numberUnits, 
			Integer warehouseLocationId,  String warehouseLocationValue, String storageRemarks,
			String description, String remarks, Class<? extends Storage> clazz,
			BigInteger wholeCountPerLb, BigDecimal smallSize, BigDecimal ws, BigDecimal lp, BigDecimal breakage, 
			BigDecimal foreignMaterial, BigDecimal humidity, BigDecimal testa,
			BigDecimal scorched, BigDecimal deepCut, BigDecimal offColour, BigDecimal shrivel, BigDecimal desert,
			BigDecimal deepSpot, BigDecimal mold, BigDecimal dirty, BigDecimal decay, BigDecimal insectDamage,
			BigDecimal roastingWeightLoss,
			CheckStatus colour, CheckStatus flavour) {
		this.rawItemQuality = new RawItemQualityDTO(id, version, 
				itemId, itemValue,
				testMU, sampleWeight, numberOfSamples,
				/* poCodeId, contractTypeCode, supplierName, */
				/* description, remarks, */
				wholeCountPerLb, smallSize, ws, lp, breakage, 
				foreignMaterial, humidity, testa,
				scorched, deepCut, offColour, shrivel, desert,
				deepSpot, mold, dirty, decay, insectDamage,
				roastingWeightLoss,
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
