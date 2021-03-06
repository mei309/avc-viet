/**
 * 
 */
package com.avc.mis.beta.entities.process.collectionItems;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.avc.mis.beta.entities.RankedAuditedEntity;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.process.group.ItemCount;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * One amount record in an item count.
 * 
 * @author zvi
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity
@Table(name = "COUNT_AMOUNTS")
@Inheritance(strategy=InheritanceType.JOINED)
public class CountAmount extends RankedAuditedEntity {

	@ToString.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "itemCountId", nullable = false, updatable = false)
	@NotNull
	private ItemCount itemCount;
	
	@Column(nullable = false, precision = 19, scale = MeasureUnit.SCALE)
	@NotNull(message = "Amount is required")
	@Positive(message = "Amount has to be positive")
	private BigDecimal amount;
	
	@Override
	public void setReference(Object referenced) {
		if(referenced instanceof ItemCount) {
			this.setItemCount((ItemCount)referenced);
		}
		else {
			throw new ClassCastException("Referenced object isn't an item count");
		}		
	}
}
