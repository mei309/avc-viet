/**
 * 
 */
package com.avc.mis.beta.entities.processinfo;

import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.groups.ConvertGroup;
import javax.validation.groups.Default;

import com.avc.mis.beta.entities.AuditedEntity;
import com.avc.mis.beta.entities.embeddable.AmountWithUnit;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.process.PoProcess;
import com.avc.mis.beta.entities.process.StorageRelocation;
import com.avc.mis.beta.entities.values.Warehouse;
import com.avc.mis.beta.validation.groups.OnPersist;
import com.avc.mis.beta.validation.groups.PositiveAmount;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author zvi
 *
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity
@Table(name = "STORAGE_MOVES")
//@Inheritance(strategy=InheritanceType.JOINED)
//@PrimaryKeyJoinColumn(name = "storageBaseId")
public class StorageMove extends StorageBase {
	
	public StorageMove() {
		super();
		setDtype("Storage");
	}
	
	@NotNull(message = "System error: Process not referenced", groups = OnPersist.class)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "processId", updatable = false)
	private StorageRelocation process;
	
	@Override
	@NotNull(message = "Internal error: Used item has no referance to storage")
	public Storage getStorage() {
		return super.getStorage();
	}
	
	@Override
	@NotNull(message = "Used number of units is required")
	@Positive(message = "Used number of units has to be positive")
	public BigDecimal getNumberUsedUnits() {
		return super.getNumberUsedUnits();
	}
	
	@Override
	public void setReference(Object referenced) {
		if(referenced instanceof StorageRelocation) {
			this.setProcess((StorageRelocation)referenced);
		}
		else {
			throw new ClassCastException("Referenced object isn't a storage relocation");
		}		
	}
	
	@JsonIgnore
	@PrePersist
	public void setProcessItem() {
		super.setProcessItem(getStorage().getProcessItem());
	}

}
