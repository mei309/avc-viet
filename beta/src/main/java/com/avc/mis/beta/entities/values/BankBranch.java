/**
 * 
 */
package com.avc.mis.beta.entities.values;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avc.mis.beta.entities.Insertable;
import com.avc.mis.beta.entities.ValueEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Zvi
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity
@Table(name="BANK_BRANCHES")
public class BankBranch extends ValueEntity {
	
	@Column(name = "name", nullable = false)
	private String value;
	
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "bankId", nullable = false)
	private Bank bank;
	
	public void setValue(String value) {
		this.value = value.trim();
	}
	
	protected boolean canEqual(Object o) {
		return Insertable.canEqualCheckNullId(this, o);
	}
	
	@JsonIgnore
	@Override
	public boolean isLegal() {
		return StringUtils.isNotBlank(getValue()) && getBank() != null;
	}
	
	@Override
	public String getIllegalMessage() {
		return "Branch name can't be blank and branch has to belong to a bank";
	}
}
