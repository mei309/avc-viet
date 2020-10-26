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
import com.avc.mis.beta.dto.query.UsedItemWithGroup;
import com.avc.mis.beta.dto.values.BasicValueEntity;
import com.avc.mis.beta.entities.Ordinal;
import com.avc.mis.beta.entities.embeddable.AmountWithUnit;
import com.avc.mis.beta.entities.enums.MeasureUnit;
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
public class UsedItemsGroupDTO extends SubjectDataDTO {

	private String groupName;

	@JsonIgnore
	private boolean tableView;
	private List<UsedItemDTO> usedItems;


	public UsedItemsGroupDTO(Integer id, Integer version, Integer ordinal,
			String groupName, boolean tableView) {
		super(id, version, ordinal);
		this.groupName = groupName;
		this.tableView = tableView;
	}	

	public UsedItemsGroupDTO(UsedItemsGroup group) {
		super(group.getId(), group.getVersion(), group.getOrdinal());
		this.groupName = group.getGroupName();
		this.tableView = group.isTableView();
		this.usedItems = (Arrays.stream(group.getUsedItems())
				.map(u->{return new UsedItemDTO(u);})
				.collect(Collectors.toList()));
	}
	
	public List<UsedItemDTO> getUsedItems() {
		if(tableView) {
			return null;
		}
		return this.usedItems;
	}
	
	public UsedItemTableDTO getUsedItem() {
		if(tableView && this.usedItems != null && !this.usedItems.isEmpty()) {
			UsedItemTableDTO usedItemTable = new UsedItemTableDTO();
			this.usedItems.stream().findAny().ifPresent(s -> {
				usedItemTable.setItem(s.getItem());
				usedItemTable.setItemPo(s.getItemPo());
				usedItemTable.setItemProcessDate(s.getItemProcessDate());
				StorageBaseDTO storage = s.getStorage();
				usedItemTable.setMeasureUnit(storage.getUnitAmount().getMeasureUnit());
				usedItemTable.setContainerWeight(storage.getContainerWeight());
				BasicValueEntity<Warehouse> warehouse = storage.getWarehouseLocation();
				if(warehouse != null)
					usedItemTable.setWarehouseLocation(new Warehouse(warehouse.getId(), warehouse.getValue()));
			});
			List<BasicUsedStorageDTO> used = this.usedItems.stream().map((i) -> {
				StorageBaseDTO storage = i.getStorage();
				return new BasicUsedStorageDTO(i.getId(), i.getVersion(), 
						storage.getId(), storage.getVersion(), storage.getOrdinal(), storage.getNumberUnits());
			}).collect(Collectors.toList());
			usedItemTable.setAmounts(used);
			return usedItemTable;
		}
		return null;
	}
	
	public AmountWithUnit[] getTotalAmount() {
		AmountWithUnit totalAmount = usedItems.stream()
				.map(ui -> ui.getStorage().getUnitAmount()
						.substract(Optional.ofNullable(ui.getStorage().getContainerWeight()).orElse(BigDecimal.ZERO))
						.multiply(ui.getNumberUsedUnits()))
				.reduce(AmountWithUnit::add).orElse(AmountWithUnit.ZERO_KG);
		return new AmountWithUnit[] {totalAmount.setScale(MeasureUnit.SCALE),
				totalAmount.convert(MeasureUnit.LOT).setScale(MeasureUnit.SCALE)};
	}
	
	public static List<UsedItemsGroupDTO> getUsedItemsGroups(List<UsedItemWithGroup> usedItems) {
		Map<Integer, List<UsedItemWithGroup>> map = usedItems.stream()
				.collect(Collectors.groupingBy(UsedItemWithGroup::getId, LinkedHashMap::new, Collectors.toList()));
		List<UsedItemsGroupDTO> usedItemsGroups = new ArrayList<>();
		for(List<UsedItemWithGroup> list: map.values()) {
			UsedItemsGroupDTO usedItemsGroup = list.get(0).getUsedItemsGroup();
			usedItemsGroup.setUsedItems(list.stream()
					.map(i -> i.getUsedItem())
//					.sorted(Ordinal.ordinalComparator())
					.collect(Collectors.toList()));
			usedItemsGroups.add(usedItemsGroup);
		}
//		usedItemsGroups.sort(Ordinal.ordinalComparator());
		return usedItemsGroups;
	}
}
