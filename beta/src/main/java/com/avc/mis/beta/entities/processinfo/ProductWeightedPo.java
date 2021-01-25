/**
 * 
 */
package com.avc.mis.beta.entities.processinfo;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avc.mis.beta.entities.ProcessInfoEntity;
import com.avc.mis.beta.entities.codes.PoCode;
import com.avc.mis.beta.entities.enums.MeasureUnit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author zvi
 *
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "PRUDUCT_WEIGHTED_POS")
public class ProductWeightedPo extends ProcessInfoEntity {
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private PoCode poCode;
	
	@Column(nullable = false, precision = 19, scale = MeasureUnit.SCALE)
	private BigDecimal weight = BigDecimal.ONE;

}