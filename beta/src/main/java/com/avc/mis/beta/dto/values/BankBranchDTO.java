/**
 * 
 */
package com.avc.mis.beta.dto.values;

import java.io.Serializable;

import com.avc.mis.beta.entities.values.BankBranch;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

/**
 * @author Zvi
 *
 */
@Value
public class BankBranchDTO implements Serializable {
	@EqualsAndHashCode.Exclude
	Integer id;
	String value;
	String bankName;
	
	@lombok.experimental.Tolerate
	public BankBranchDTO(@NonNull BankBranch branch) {
		this.id = branch.getId();
		this.value = branch.getValue();
		this.bankName = branch.getBank().getValue();
	}
}
