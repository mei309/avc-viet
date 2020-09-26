package com.avc.mis.beta.entities.processinfo;

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

import com.avc.mis.beta.entities.AuditedEntity;
import com.avc.mis.beta.entities.enums.MeasureUnit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents items used for processing (input items of a process). 
 * Used items refer to existing stored inventory.
 * e.g. raw cashew, sugar, oil, bags etc.
 * 
 * @author zvi
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity
@Table(name = "USED_ITEMS")
@Inheritance(strategy=InheritanceType.JOINED)
public class UsedItem extends UsedItemBase {
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "groupId")
	@NotNull(message = "Used items have to belong to a group categery")
	private UsedItemsGroup group;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "storageId")
	@NotNull(message = "Internal error: Used item has no referance to storage")
	private Storage storage;
	
	@Column(nullable = false, precision = 19, scale = MeasureUnit.SCALE)
	@NotNull(message = "Used number of units is required")
	@Positive(message = "Used number of units has to be positive")
	private BigDecimal usedUnits = BigDecimal.ONE;	

//	protected boolean canEqual(Object o) {
//		return Insertable.canEqualCheckNullId(this, o);
//	}
	
	@Override
	public void setReference(Object referenced) {
		if(referenced instanceof UsedItemsGroup) {
			this.setGroup((UsedItemsGroup)referenced);
		}
		else {
			throw new ClassCastException("Referenced object isn't a used item group");
		}		
	}
	
}
