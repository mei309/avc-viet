/**
 * 
 */
package com.avc.mis.beta.dto.processinfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.avc.mis.beta.dto.ProcessDTO;
import com.avc.mis.beta.dto.query.ItemCountWithAmount;
import com.avc.mis.beta.dto.query.ProcessItemWithStorage;
import com.avc.mis.beta.dto.values.ItemDTO;
import com.avc.mis.beta.entities.embeddable.AmountWithUnit;
import com.avc.mis.beta.entities.enums.ItemCategory;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.processinfo.ItemCount;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zvi
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ItemCountDTO extends ProcessDTO {

	private ItemDTO item;
	private MeasureUnit measureUnit;
	private BigDecimal containerWeight;	
	
	private List<CountAmountDTO> amounts;

	public ItemCountDTO(Integer id, Integer version, 
			Integer itemId, String itemValue, ItemCategory itemCategory,
			MeasureUnit measureUnit, BigDecimal containerWeight) {
		super(id, version);
		this.item = new ItemDTO(itemId, itemValue, null, null, itemCategory);
		this.measureUnit = measureUnit;
		this.containerWeight = containerWeight;
	}

	/**
	 * @param itemCount
	 */
	public ItemCountDTO(ItemCount itemCount) {
		super(itemCount.getId(), itemCount.getVersion());
		this.item = new ItemDTO(itemCount.getItem());
		this.measureUnit = itemCount.getMeasureUnit();
		this.containerWeight = itemCount.getContainerWeight();
		setAmounts(Arrays.stream(itemCount.getAmounts())
				.map(i->{return new CountAmountDTO(i);})
				.collect(Collectors.toList()));
	}
	
	public static List<ItemCountDTO> getItemCounts(List<ItemCountWithAmount> amounts) {
		Map<Integer, List<ItemCountWithAmount>> map = amounts.stream()
				.collect(Collectors.groupingBy(ItemCountWithAmount::getId, Collectors.toList()));
		List<ItemCountDTO> itemCounts = new ArrayList<>();
		for(List<ItemCountWithAmount> list: map.values()) {
			ItemCountDTO itemCount = list.get(0).getItemCount();
			itemCount.setAmounts(list.stream().map(i -> i.getAmount())
					.collect(Collectors.toList()));
			itemCounts.add(itemCount);
		}
		return itemCounts;
	}
	
	
}
