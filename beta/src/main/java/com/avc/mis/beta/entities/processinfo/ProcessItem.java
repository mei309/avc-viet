/**
 * 
 */
package com.avc.mis.beta.entities.processinfo;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Where;

import com.avc.mis.beta.dto.processinfo.BasicStorageDTO;
import com.avc.mis.beta.dto.processinfo.StorageTableDTO;
import com.avc.mis.beta.dto.processinfo.UsedItemDTO;
import com.avc.mis.beta.entities.Insertable;
import com.avc.mis.beta.entities.Ordinal;
import com.avc.mis.beta.entities.ProcessInfoEntity;
import com.avc.mis.beta.entities.embeddable.AmountWithUnit;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.values.Item;
import com.avc.mis.beta.entities.values.Warehouse;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents an Item that the process adds to stock. perhaps name should be changed to InItem/ImportItem/AddedItem
 * 
 * @author Zvi
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity
@Table(name = "PROCESSED_ITEMS")
@Inheritance(strategy=InheritanceType.JOINED)
public class ProcessItem extends ProcessInfoEntity {
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "itemId", nullable = false)
	@NotNull(message = "Item is mandatory")
	private Item item;

	@Setter(value = AccessLevel.NONE) @Getter(value = AccessLevel.NONE)
	@OneToMany(mappedBy = "processItem", targetEntity=StorageBase.class, orphanRemoval = true, 
		cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.LAZY)
	@Where(clause = "dtype = 'Storage'")
//	@NotEmpty(message = "Process line has to contain at least one storage line") //made a bug when using merge for persisting ProcessItem
	private Set<Storage> storageForms;
	
	@JsonIgnore
	@ToString.Exclude 
	@OneToMany(mappedBy = "processItem", fetch = FetchType.LAZY)
	private Set<StorageBase> allStorags;
	
	@Setter(value = AccessLevel.NONE) 
	@JsonIgnore
	@Column(nullable = false)
	private boolean tableView = false;
	
	private String groupName;
	
	public void setGroupName(String groupName) {
		this.groupName = Optional.ofNullable(groupName).map(s -> s.trim()).orElse(null);
	}
	
	/**
	 * Gets the list of Storage forms as an array (can be ordered).
	 * @return the storageForms
	 */
	public Storage[] getStorageForms() {
		return this.storageForms.toArray(new Storage[this.storageForms.size()]);
	}
	
	@JsonIgnore
	Set<Storage> getStorageFormsField() {
		return this.storageForms;
	}

	/**
	 * Setter for adding Storage forms for items that are processed, 
	 * receives an array (which can be ordered, for later use to add an order to the items).
	 * Filters the not legal items and set needed references to satisfy needed foreign keys of database.
	 * @param storageForms array of Storages to set
	 */
	public void setStorageForms(Storage[] storageForms) {
		Ordinal.setOrdinals(storageForms);
		this.storageForms = Insertable.setReferences(storageForms, (t) -> {t.setReference(this);	return t;});
	}
	
	/**
	 * Setter for adding Storage forms for items that are processed, 
	 * receives an array (which can be ordered, for later use to add an order to the items).
	 * Filters the not legal items and set needed references to satisfy needed foreign keys of database.
	 * @param storageForms array of StorageDTOs to set
	 */
	public void setUsedItems(UsedItemDTO[] usedItems) {
		try {
			setStorageForms((Storage[]) Arrays.stream(usedItems)
					.map(i -> i.getStorage().getNewStorage(i.getNumberUsedUnits(), i.getNewLocation()))
					.toArray());
		} catch (NullPointerException e) {
			throw new NullPointerException("Used item storage is null");
		}		
	}
	
//	public void setStorage(UsedItemTableDTO usedItemTable) {
//		this.tableView = true;
//		
//		List<BasicUsedStorageDTO> basicUsedStorages = usedItemTable.getAmounts();
//		UsedItemDTO[] usedItems = new UsedItemDTO[basicUsedStorages.size()];
//		
//		for(int i=0; i<usedItems.length; i++) {
//			BasicUsedStorageDTO basicUsedStorage = basicUsedStorages.get(i);
//			usedItems[i] = new UsedItemDTO();
//			usedItems[i].setId(basicUsedStorage.getId());
//			usedItems[i].setVersion(basicUsedStorage.getVersion());
//			Storage storage = new Storage();
//			storage.setId(basicUsedStorage.getStorageId());
//			storage.setVersion(basicUsedStorage.getStorageVersion());
//			usedItems[i].setStorage(storage);
//		}
//
//		MeasureUnit measureUnit = storageTable.getMeasureUnit();
//		BigDecimal containerWeight = storageTable.getContainerWeight();
//		Warehouse warehouse = storageTable.getWarehouseLocation();
//		List<BasicStorageDTO> amounts = storageTable.getAmounts();
//		Storage[] storageForms = new Storage[amounts.size()];
//		for(int i=0; i<storageForms.length; i++) {
//			BasicStorageDTO amount = amounts.get(i);
//			storageForms[i] = new Storage();
//			storageForms[i].setOrdinal(amount.getOrdinal());
//			storageForms[i].setUnitAmount(new AmountWithUnit(amount.getAmount(), measureUnit));
//			storageForms[i].setContainerWeight(containerWeight);
//			storageForms[i].setWarehouseLocation(warehouse);
//			storageForms[i].setReference(this);
//		}
//		setStorageForms(usedItems);
//		
//	}
	
	/**
	 * Setter for adding list of Storage forms that share the same common measure unit, 
	 * empty container weight and each only have one unit.
	 * Usefully presented in a table or list of only ordinal (number) and amount,
	 * since they all share all other parameters.
	 * @param storageTable
	 */
	public void setStorage(StorageTableDTO storageTable) {
		this.tableView = true;
		
		MeasureUnit measureUnit = storageTable.getMeasureUnit();
		BigDecimal containerWeight = storageTable.getContainerWeight();
		Warehouse warehouse = storageTable.getWarehouseLocation();
		List<BasicStorageDTO> amounts = storageTable.getAmounts();
		Storage[] storageForms = new Storage[amounts.size()];
		for(int i=0; i<storageForms.length; i++) {
			BasicStorageDTO amount = amounts.get(i);
			storageForms[i] = new Storage();
			storageForms[i].setOrdinal(amount.getOrdinal());
			storageForms[i].setUnitAmount(new AmountWithUnit(amount.getAmount(), measureUnit));
			storageForms[i].setContainerWeight(containerWeight);
			storageForms[i].setWarehouseLocation(warehouse);
			storageForms[i].setReference(this);
		}
		setStorageForms(storageForms);
		
	}
	
	/**
	 * Used by Lombok so new/transient entities with null id won't be equal.
	 * @param o
	 * @return false if both this object's and given object's id is null 
	 * or given object is not of the same class, otherwise returns true.
	 */
//	protected boolean canEqual(Object o) {
//		return Insertable.canEqualCheckNullId(this, o);
//	}
	
}
