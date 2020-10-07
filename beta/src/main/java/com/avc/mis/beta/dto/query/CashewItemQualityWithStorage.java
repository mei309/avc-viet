/**
 * 
 */
package com.avc.mis.beta.dto.query;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.avc.mis.beta.dto.processinfo.CashewItemQualityDTO;
import com.avc.mis.beta.dto.processinfo.StorageBaseDTO;
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
public class CashewItemQualityWithStorage {
	
	private CashewItemQualityDTO cashewItemQuality;
	private StorageBaseDTO storage;

	public CashewItemQualityWithStorage(Integer id, Integer version, Integer ordinal,
			Integer itemId, String itemValue, 
			MeasureUnit testMU, BigDecimal sampleWeight, BigInteger numberOfSamples, boolean presentage,
			/* Integer poCodeId, ContractTypeCode contractTypeCode, String supplierName, */
			Integer storageId, Integer storageVersion, Integer storageOrdinal,
			BigDecimal unitAmount, MeasureUnit measureUnit, BigDecimal numberUnits, BigDecimal containerWeight,
			Integer warehouseLocationId,  String warehouseLocationValue, String storageRemarks,
			String description, String remarks, Class<? extends Storage> clazz,
			BigInteger wholeCountPerLb, BigDecimal smallSize, BigDecimal ws, BigDecimal lp, BigDecimal breakage, 
			BigDecimal foreignMaterial, BigDecimal humidity, BigDecimal testa,
			BigDecimal scorched, BigDecimal deepCut, BigDecimal offColour, BigDecimal shrivel, BigDecimal desert,
			BigDecimal deepSpot, BigDecimal mold, BigDecimal dirty, BigDecimal lightDirty, 
			BigDecimal decay, BigDecimal insectDamage,
			BigDecimal roastingWeightLoss,
			CheckStatus colour, CheckStatus flavour) {
		this.cashewItemQuality = new CashewItemQualityDTO(id, version, ordinal,
				itemId, itemValue,
				testMU, sampleWeight, numberOfSamples, presentage,
				/* poCodeId, contractTypeCode, supplierName, */
				/* description, remarks, */
				wholeCountPerLb, smallSize, ws, lp, breakage, 
				foreignMaterial, humidity, testa,
				scorched, deepCut, offColour, shrivel, desert,
				deepSpot, mold, dirty, lightDirty, decay, insectDamage,
				roastingWeightLoss,
				colour, flavour);
		this.storage = new StorageBaseDTO(storageId, storageVersion, storageOrdinal, 
				unitAmount, measureUnit, numberUnits, containerWeight,
				warehouseLocationId, warehouseLocationValue, storageRemarks, clazz);
	}
	
	
	
	/**
	 * @return id of RawItemQuality. 
	 * Used for mapping to the logical structure that every RawItemQuality has a collection of storages.
	 */
	public Integer getId() {
		return cashewItemQuality.getId();
	}
	
}