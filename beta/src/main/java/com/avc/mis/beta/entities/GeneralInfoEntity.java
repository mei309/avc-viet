/**
 * 
 */
package com.avc.mis.beta.entities;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.avc.mis.beta.entities.process.GeneralProcess;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Abstract class for entities representing information notifying about general processes -
 * processes that aren't bound to a specific po code.
 * (For information about processes of a po see ProcessInfoEntity class)
 * e.g. message about a process transaction or management info. 
 * 
 * @author Zvi
 *
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@MappedSuperclass
public abstract class GeneralInfoEntity extends AuditedEntity {
	
	@ToString.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "processId", updatable = false)
	private GeneralProcess process;
	
	private String description;
	
	@Override
	public void setReference(Object referenced) {
		if(referenced instanceof GeneralProcess) {
			this.setProcess((GeneralProcess)referenced);
		}
		else {
			throw new ClassCastException("Referenced object isn't a process");
		}		
	}
	
}
