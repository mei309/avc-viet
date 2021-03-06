/**
 * 
 */
package com.avc.mis.beta.dto.query;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import com.avc.mis.beta.dto.basic.BasicValueEntity;
import com.avc.mis.beta.dto.basic.PoCodeBasic;
import com.avc.mis.beta.entities.codes.BasePoCode;
import com.avc.mis.beta.entities.embeddable.AmountWithUnit;
import com.avc.mis.beta.entities.enums.ItemGroup;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.enums.ProductionUse;
import com.avc.mis.beta.entities.system.WeightedPo;
import com.avc.mis.beta.entities.values.Item;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.ToString;
import lombok.Value;

/**
 * For fetching with query items with their amounts and po, for setting po weights.
 * 
 * @author zvi
 *
 */
@Value
public class ItemAmountWithPoCode {

	PoCodeBasic poCode;

	BasicValueEntity<Item> item;

	@ToString.Exclude @JsonIgnore
	ItemGroup itemGroup;
	AmountWithUnit weightAmount;
	AmountWithUnit amount;
	
	public ItemAmountWithPoCode(
			Integer poCodeId, String poCodeCode, 
			String contractTypeCode, String contractTypeSuffix, String supplierName, 
			Integer itemId, String itemValue, MeasureUnit defaultMeasureUnit, 
			ItemGroup itemGroup, ProductionUse productionUse, 
			BigDecimal unitAmount, MeasureUnit unitMeasureUnit, Class<? extends Item> clazz, 
			BigDecimal amount) {
		super();
		this.poCode = new PoCodeBasic(poCodeId, poCodeCode, contractTypeCode, contractTypeSuffix, supplierName);
		this.item = new BasicValueEntity<Item>(itemId, itemValue);
		this.itemGroup = itemGroup;
		if(MeasureUnit.NONE == unitMeasureUnit && MeasureUnit.WEIGHT_UNITS.contains(defaultMeasureUnit)) {
			this.amount = null;
			this.weightAmount = new AmountWithUnit(amount, defaultMeasureUnit);
		}
		else if(MeasureUnit.WEIGHT_UNITS.contains(unitMeasureUnit))
		{
			this.amount = new AmountWithUnit(amount, defaultMeasureUnit);
			this.weightAmount = new AmountWithUnit(
					amount.multiply(unitAmount, MathContext.DECIMAL64), 
					unitMeasureUnit).setScale(MeasureUnit.SCALE);
//			this.amount.setScale(MeasureUnit.SCALE);
		}
		else 
		{
			this.amount = new AmountWithUnit(amount, defaultMeasureUnit);
			this.weightAmount = null;
		}
	}
	
	@JsonIgnore
	public static WeightedPo getWeightedPo(Integer poCodeId) {
		WeightedPo weightedPo = new WeightedPo();
		BasePoCode poCode = new BasePoCode();
		poCode.setId(poCodeId);
		weightedPo.setPoCode(poCode);
		return weightedPo;
	}
	
	@JsonIgnore
	static AmountWithUnit getTotalWeight(List<ItemAmountWithPoCode> itemAmounts) {
		return itemAmounts.stream().map(i -> i.getWeightAmount()).reduce(AmountWithUnit::add).get();
	}
	
}
