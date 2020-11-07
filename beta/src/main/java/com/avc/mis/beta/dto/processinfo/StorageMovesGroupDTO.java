/**
 * 
 */
package com.avc.mis.beta.dto.processinfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.avc.mis.beta.dto.SubjectDataDTO;
import com.avc.mis.beta.dto.query.StorageMoveWithGroup;
import com.avc.mis.beta.dto.query.UsedItemWithGroup;
import com.avc.mis.beta.dto.values.BasicValueEntity;
import com.avc.mis.beta.entities.Ordinal;
import com.avc.mis.beta.entities.embeddable.AmountWithUnit;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.processinfo.StorageMovesGroup;
import com.avc.mis.beta.entities.processinfo.UsedItemsGroup;
import com.avc.mis.beta.entities.values.Warehouse;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zvi
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StorageMovesGroupDTO extends SubjectDataDTO {

	private String groupName;

	@JsonIgnore
	private boolean tableView;
	private List<StorageMoveDTO> storageMoves;


	public StorageMovesGroupDTO(Integer id, Integer version, Integer ordinal,
			String groupName, boolean tableView) {
		super(id, version, ordinal);
		this.groupName = groupName;
		this.tableView = tableView;
	}	

	public StorageMovesGroupDTO(StorageMovesGroup group) {
		super(group.getId(), group.getVersion(), group.getOrdinal());
		this.groupName = group.getGroupName();
		this.tableView = group.isTableView();
		this.storageMoves = (Arrays.stream(group.getStorageMoves())
				.map(u->{return new StorageMoveDTO(u);})
				.collect(Collectors.toList()));
	}
	
	public List<StorageMoveDTO> getStorageMoves() {
		if(tableView) {
			return null;
		}
		return this.storageMoves;
	}
	
	public MovedItemTableDTO getStorageMove() {
		if(tableView && this.storageMoves != null && !this.storageMoves.isEmpty()) {
			MovedItemTableDTO movedItemTable = new MovedItemTableDTO();
			this.storageMoves.stream().findAny().ifPresent(s -> {
				movedItemTable.setItem(s.getItem());
				movedItemTable.setItemPo(s.getItemPo());
				BasicValueEntity<Warehouse> warehouse = s.getWarehouseLocation();
				if(warehouse != null)
					movedItemTable.setNewWarehouseLocation(new Warehouse(warehouse.getId(), warehouse.getValue()));
				StorageBaseDTO storage = s.getStorage();
				movedItemTable.setMeasureUnit(storage.getUnitAmount().getMeasureUnit());
				movedItemTable.setContainerWeight(storage.getContainerWeight());
				warehouse = storage.getWarehouseLocation();
				if(warehouse != null)
					movedItemTable.setWarehouseLocation(new Warehouse(warehouse.getId(), warehouse.getValue()));
				
			});
			
			List<BasicUsedStorageDTO> used = this.storageMoves.stream().map((s) -> {
				StorageBaseDTO storage = s.getStorage();
				return new BasicUsedStorageDTO(s.getId(), s.getVersion(), 
						storage.getId(), storage.getVersion(), storage.getOrdinal(), storage.getNumberUnits());
			}).collect(Collectors.toList());
			movedItemTable.setAmounts(used);
			return movedItemTable;
		}
		return null;
	}
	
	public AmountWithUnit[] getTotalAmount() {
		AmountWithUnit totalAmount = storageMoves.stream()
				.map(ui -> ui.getStorage().getUnitAmount()
						.substract(Optional.ofNullable(ui.getStorage().getContainerWeight()).orElse(BigDecimal.ZERO))
						.multiply(ui.getNumberUsedUnits()))
				.reduce(AmountWithUnit::add).orElse(AmountWithUnit.ZERO_KG);
		return new AmountWithUnit[] {totalAmount.setScale(MeasureUnit.SCALE),
				totalAmount.convert(MeasureUnit.LOT).setScale(MeasureUnit.SCALE)};
	}
	
	public static List<StorageMovesGroupDTO> getStorageMoveGroups(List<StorageMoveWithGroup> storageMoves) {
		Map<Integer, List<StorageMoveWithGroup>> map = storageMoves.stream()
				.collect(Collectors.groupingBy(StorageMoveWithGroup::getId, LinkedHashMap::new, Collectors.toList()));
		List<StorageMovesGroupDTO> storageMovesGroups = new ArrayList<>();
		for(List<StorageMoveWithGroup> list: map.values()) {
			StorageMovesGroupDTO storageMovesGroup = list.get(0).getStorageMovesGroup();
			storageMovesGroup.setStorageMoves(list.stream()
					.map(i -> i.getStorageMove())
//					.sorted(Ordinal.ordinalComparator())
					.collect(Collectors.toList()));
			storageMovesGroups.add(storageMovesGroup);
		}
//		usedItemsGroups.sort(Ordinal.ordinalComparator());
		return storageMovesGroups;
	}
}