/**
 * 
 */
package com.avc.mis.beta.dto.processinfo;

import java.math.BigDecimal;

import com.avc.mis.beta.dto.SubjectDataDTO;
import com.avc.mis.beta.dto.values.PoCodeBasic;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.processinfo.ProductWeightedPo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zvi
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductWeightedPoDTO extends SubjectDataDTO {

	private PoCodeBasic poCode;
	private BigDecimal weight;
	
	public ProductWeightedPoDTO(Integer id, Integer version, Integer ordinal,
			Integer poCodeId, String poCodeCode, String contractTypeCode, String contractTypeSuffix, String supplierName, String display,
			BigDecimal weight) {
		super(id, version, ordinal);
		if(poCodeId != null)
			this.poCode = new PoCodeBasic(poCodeId, poCodeCode, contractTypeCode, contractTypeSuffix, supplierName, display);
		this.weight = weight;
	}
	
	public ProductWeightedPoDTO(ProductWeightedPo weightedPo) {
		super(weightedPo.getId(), weightedPo.getVersion(), weightedPo.getOrdinal());
		this.poCode = new PoCodeBasic(weightedPo.getPoCode());
		this.weight = weightedPo.getWeight().setScale(MeasureUnit.SCALE);
	}

}