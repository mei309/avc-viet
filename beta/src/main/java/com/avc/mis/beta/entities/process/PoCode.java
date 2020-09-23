/**
 * 
 */
package com.avc.mis.beta.entities.process;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import com.avc.mis.beta.entities.BaseEntity;
import com.avc.mis.beta.entities.data.Supplier;
import com.avc.mis.beta.entities.values.ContractType;
import com.avc.mis.beta.validation.groups.OnPersist;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Basic immutable information that serves as identification for material/s, product/s 
 * that where usually aggregated in one purchase order and followed during processing life as a separate unit.
 * 
 * code and id are synonymous in this class
 * 
 * @author Zvi
 *
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "PO_CODES")
public class PoCode extends BaseEntity {
	
	@Id
	@GenericGenerator(name = "UseExistingIdOtherwiseGenerateUsingIdentity", strategy = "com.avc.mis.beta.utilities.UseExistingIdOtherwiseGenerateUsingIdentity")
	@GeneratedValue(generator = "UseExistingIdOtherwiseGenerateUsingIdentity")
	@Column(nullable = false, updatable = false)
	@EqualsAndHashCode.Include
	private Integer code;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "supplierId", updatable = false, nullable = false)
	private Supplier supplier; 
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contractTypeId", updatable = false, nullable = false)
	@NotNull(message = "PO code is required to have a contract type", groups = OnPersist.class)
	private ContractType contractType;
	
	/**
	 * @return a string representing full PO code. e.g. VAT-900001, PO-900001V
	 */
	public String getValue() {
		return String.format("%s-%d%s", this.contractType.getCode(), this.code, this.contractType.getSuffix());
	}

	@Override
	public Integer getId() {
		return code;
	}

	@Override
	public void setId(Integer id) {
		this.code = id;		
	}
	
	/**
	 * Used by Lombok so new/transient entities with null id won't be equal.
	 * @param o
	 * @return false if both this object's and given object's id is null 
	 * or given object is not of the same class, otherwise returns true.
	 */
//	@JsonIgnore
//	protected boolean canEqual(Object o) {
//		return Insertable.canEqualCheckNullId(this, o);
//	}

}
