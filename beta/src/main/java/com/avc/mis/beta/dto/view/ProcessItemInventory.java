/**
 * 
 */
package com.avc.mis.beta.dto.view;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.avc.mis.beta.dto.BasicDataDTO;
import com.avc.mis.beta.dto.basic.BasicValueEntity;
import com.avc.mis.beta.dto.basic.ItemWithUnitDTO;
import com.avc.mis.beta.dto.basic.PoCodeBasic;
import com.avc.mis.beta.dto.process.storages.BasicStorageDTO;
import com.avc.mis.beta.dto.process.storages.StorageTableDTO;
import com.avc.mis.beta.entities.embeddable.AmountWithUnit;
import com.avc.mis.beta.entities.enums.ItemGroup;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.values.CashewGrade;
import com.avc.mis.beta.entities.values.Item;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO of inventory info needed for display of process item - item resulted from process.
 * Includes information about process item , process and list of StorageInventoryRow
 * that contains storage information of the processed item.
 * 
 * @author Zvi
 *
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class ProcessItemInventory extends BasicDataDTO {

	private ItemWithUnitDTO item;
	private MeasureUnit measureUnit;
	private PoCodeBasic poCode;
	private String[] poCodes;
	private String[] suppliers;
	private BasicValueEntity<CashewGrade> cashewGrade;
	private LocalDateTime itemProcessDate;
	private LocalDateTime receiptDate;
	
	@JsonIgnore
	private boolean tableView;
	private List<StorageInventoryRow> storageForms;
	
	/**
	 * All database fields (the fields in the form they are fetched from the db) arguments constructor, 
	 * excluding list of storage forms and calculated totals.
	 */
	public ProcessItemInventory(Integer id, Integer version, 
			Integer itemId, String itemValue, MeasureUnit itemMeasureUnit, ItemGroup itemGroup, 
			AmountWithUnit unit, Class<? extends Item> clazz,
			MeasureUnit processItemMeasureUnit, 
			Integer poCodeId, String poCodeCode, String contractTypeCode, String contractTypeSuffix, String supplierName, 
			String poCodes, String suppliers, 
			Integer gradeId,  String gradeValue, 
			LocalDateTime processDate, LocalDateTime receiptDate, boolean tableView) {
		super(id, version);
		this.item = new ItemWithUnitDTO(itemId, itemValue, itemMeasureUnit, itemGroup, null, unit, clazz);
		this.measureUnit = processItemMeasureUnit;
		this.poCode = new PoCodeBasic(poCodeId, poCodeCode, contractTypeCode, contractTypeSuffix, supplierName);
		if(poCodes != null)
			this.poCodes = Stream.of(poCodes.split(",")).distinct().toArray(String[]::new);
		if(suppliers != null)
			this.suppliers = Stream.of(suppliers.split(",")).distinct().toArray(String[]::new);
		if(gradeId != null && gradeValue != null)
			this.cashewGrade = new BasicValueEntity<CashewGrade>(gradeId, gradeValue);
		else
			this.cashewGrade = null;
		this.itemProcessDate = processDate;
		this.receiptDate = receiptDate;
		this.tableView = tableView;
	}
		
	public List<StorageInventoryRow> getStorageForms() {
		if(tableView) {
			return null;
		}
		return this.storageForms;
	}
	
	public StorageTableDTO getStorage() {
		if(tableView && this.storageForms != null && !this.storageForms.isEmpty()) {
			StorageTableDTO storageTable = new StorageTableDTO();
			this.storageForms.stream().findAny().ifPresent(s -> {
				storageTable.setWarehouseLocation(s.getWarehouseLocation());
			});
			List<BasicStorageDTO> amounts = this.storageForms.stream().map((s) -> {
				return new BasicStorageDTO(s.getId(), s.getVersion(), s.getOrdinal(), s.getNumberUnits(), s.getNumberAvailableUnits());
			}).collect(Collectors.toList());
			storageTable.setAmounts(amounts);
			return storageTable;
		}
		return null;
	}
}
