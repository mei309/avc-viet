/**
 * 
 */
package com.avc.mis.beta.dto.processinfo;

import java.math.BigDecimal;
import java.util.Optional;

import com.avc.mis.beta.dto.ProcessDTO;
import com.avc.mis.beta.dto.process.PoCodeDTO;
import com.avc.mis.beta.dto.values.ItemDTO;
import com.avc.mis.beta.entities.embeddable.AmountWithUnit;
import com.avc.mis.beta.entities.enums.ItemCategory;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.processinfo.LoadedItem;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zvi
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LoadedItemDTO extends ProcessDTO {
	
	private ItemDTO item; //change to itemDTO in order to get category
	private PoCodeDTO poCode;
	private AmountWithUnit declaredAmount;
	
	private String description;
	private String remarks;
		
	public LoadedItemDTO(Integer id, Integer version, 
			Integer itemId, String itemValue, ItemCategory itemCategory,			
			Integer poCodeId, String contractTypeCode, String contractTypeSuffix, String supplierName, 
			BigDecimal declaredAmount, MeasureUnit measureUnit,
			String description, String remarks) {
		super(id, version);
		this.item = new ItemDTO(itemId, itemValue, null, null, itemCategory);
		this.poCode = new PoCodeDTO(poCodeId, contractTypeCode, contractTypeSuffix, supplierName);	
		this.declaredAmount = new AmountWithUnit(declaredAmount.setScale(MeasureUnit.SCALE), measureUnit);
		this.description = description;
		this.remarks = remarks;
	}	
	
	/**
	 * @param processItem
	 */
	public LoadedItemDTO(LoadedItem loadedItem) {
		super(loadedItem.getId(), loadedItem.getVersion());
		this.poCode = new PoCodeDTO(loadedItem.getPoCode());
		this.declaredAmount = Optional.ofNullable(loadedItem.getDeclaredAmount()).map(i -> i.setScale(MeasureUnit.SCALE)).orElse(null);
		this.description = loadedItem.getDescription();
		this.remarks = loadedItem.getRemarks();
	}

	public LoadedItemDTO(Integer id, Integer version,
			ItemDTO item, PoCodeDTO poCode, AmountWithUnit declaredAmount,
			String description, String remarks) {
		super(id, version);
		this.item = item;
		this.declaredAmount = declaredAmount;
		this.description = description;
		this.remarks = remarks;
	}

//	public static List<LoadedItemDTO> getLoadedItems(List<LoadedItemWithStorage> storages) {
//		Map<Integer, List<LoadedItemWithStorage>> map = storages.stream()
//				.collect(Collectors.groupingBy(LoadedItemWithStorage::getId, Collectors.toList()));
//		List<LoadedItemDTO> loadedItems = new ArrayList<>();
//		for(List<LoadedItemWithStorage> list: map.values()) {
//			LoadedItemDTO loadedItem = list.get(0).getLoadedItem();
//			loadedItem.setStorageForms(list.stream().map(i -> i.getStorage())
//					.collect(Collectors.toCollection(() -> new TreeSet<StorageDTO>(Ordinal.ordinalComparator()))));
//			loadedItems.add(loadedItem);
//		}
//		return loadedItems;
//	}
	
}
