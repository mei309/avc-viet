/**
 * 
 */
package com.avc.mis.beta.entities.codes;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Null;

import com.avc.mis.beta.entities.data.Supplier;
import com.avc.mis.beta.entities.values.ContractType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author zvi
 *
 */
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Table(name = "MIXED_PO_CODES")
//@PrimaryKeyJoinColumn(name = "poCodeId")
//@DiscriminatorValue("mix_code")
@Deprecated
public class MixPoCode extends BasePoCode {

	@ToString.Exclude 
	@JoinTable(name = "MIX_POS", 
		uniqueConstraints = @UniqueConstraint(columnNames = { "mixedId", "poId" }),
			joinColumns = @JoinColumn(name = "mixedId", referencedColumnName = "id"), 
			inverseJoinColumns = @JoinColumn(name = "poId", referencedColumnName = "id"))
	@ManyToMany(fetch = FetchType.LAZY)
	private Set<ProductPoCode> origionPoCodes = new HashSet<>();
	
	@Null(message = "mixed po code doesn't have a code")
	@Override
	public String getCode() {
		return super.getCode();
	}
	
	@Null(message = "mixed po code doesn't have a supplier")
	@Override
	public Supplier getSupplier() {
		return super.getSupplier();
	}
	
	@Null(message = "mixed po code doesn't have a contract type")
	@Override
	public ContractType getContractType() {
		return super.getContractType();
	}

}
