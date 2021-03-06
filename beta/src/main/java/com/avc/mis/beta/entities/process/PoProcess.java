/**
 * 
 */
package com.avc.mis.beta.entities.process;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.avc.mis.beta.entities.Insertable;
import com.avc.mis.beta.entities.codes.BasePoCode;
import com.avc.mis.beta.entities.process.group.ItemCount;
import com.avc.mis.beta.entities.system.ProcessParent;
import com.avc.mis.beta.entities.system.WeightedPo;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

/**
 * Process that also refers to a specific single PO#
 * 
 * @author Zvi
 * 
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "PO_PROCESSES")
@PrimaryKeyJoinColumn(name = "processId")
public abstract class PoProcess extends GeneralProcess {	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false, name = "po_code_code")
	private BasePoCode poCode;
	
	@Setter(value = AccessLevel.NONE) 
	@OneToMany(mappedBy = "process", 
		cascade = {CascadeType.REMOVE}, 
		fetch = FetchType.LAZY)
	private Set<WeightedPo> weightedPos = new HashSet<>();
	
	@JsonIgnore
	@ToString.Exclude 
	@OneToMany(mappedBy = "process", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
	private Set<ProcessParent> processParents = new HashSet<>();
	
	@JsonIgnore
	@ToString.Exclude 
	@OneToMany(mappedBy = "usedProcess", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
	private Set<ProcessParent> processChildren;
		
	@OneToMany(mappedBy = "process", orphanRemoval = true, 
		cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.LAZY)
	private Set<ItemCount> itemCounts = new HashSet<>();


	/**
	 * Setter for adding item counts, 
	 * receives an array (which can be ordered, for later use to add an order to the item counts).
	 * Filters the not legal items and set needed references to satisfy needed foreign keys of database.
	 * @param itemCounts the itemCounts to set
	 */
	public void setItemCounts(Set<ItemCount> itemCounts) {
//		Ordinal.setOrdinals(itemCounts);
		this.itemCounts = Insertable.setReferences(itemCounts, (t) -> {t.setReference(this);	return t;});
	}
	
}
