/**
 * 
 */
package com.avc.mis.beta.service.report.row;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import com.avc.mis.beta.dto.values.ItemWithUnitDTO;
import com.avc.mis.beta.entities.embeddable.AmountWithUnit;
import com.avc.mis.beta.entities.enums.CashewGrade;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.enums.SaltLevel;
import com.avc.mis.beta.entities.item.Item;
import com.avc.mis.beta.entities.item.ItemGroup;
import com.avc.mis.beta.entities.item.ProductionUse;

import lombok.Data;
import lombok.Getter;
import lombok.Value;

/**
 * @author zvi
 *
 */
@Data
public class CashewBaggedInventoryRow {

	private ItemWithUnitDTO item;
	private String brand;
	private String code;
	private boolean whole;
	private CashewGrade grade;
	private AmountWithUnit bagSize;
	private SaltLevel saltLevel;
	private int bagsInBox;
	private AmountWithUnit totalAmount;//amount of boxes
	private BigDecimal weightCoefficient;
	
	public CashewBaggedInventoryRow(
			Integer itemId, String itemValue, MeasureUnit defaultMeasureUnit, 
			ItemGroup itemGroup, ProductionUse productionUse, 
			AmountWithUnit unit, Class<? extends Item> clazz,
			String brand, String code, 
			boolean whole, CashewGrade grade, SaltLevel saltLevel, int numBags, 
			BigDecimal amount, MeasureUnit measureUnit) {
		this(itemId, itemValue, defaultMeasureUnit, 
				itemGroup, productionUse, 
				unit, clazz, 
				brand, code, 
				whole, grade, saltLevel, numBags, 
				amount, measureUnit, BigDecimal.ONE);
	}
	
	public CashewBaggedInventoryRow(
			Integer itemId, String itemValue, MeasureUnit defaultMeasureUnit, 
			ItemGroup itemGroup, ProductionUse productionUse, 
			AmountWithUnit unit, Class<? extends Item> clazz,
			String brand, String code, 
			boolean whole, CashewGrade grade, SaltLevel saltLevel, int numBags, 
			BigDecimal amount, MeasureUnit measureUnit, BigDecimal weightCoefficient) {
		super();
		this.item = new ItemWithUnitDTO(itemId, itemValue, defaultMeasureUnit, itemGroup, productionUse, unit, clazz);
		this.brand = brand;
		this.code = code;
		this.whole = whole;
		this.grade = grade;
		this.saltLevel = saltLevel;
		this.bagsInBox = numBags;
		this.bagSize = this.item.getUnit().divide(BigDecimal.valueOf(numBags));
		this.totalAmount = new AmountWithUnit(amount, measureUnit);
		this.weightCoefficient = weightCoefficient;
	}
	
	public BigDecimal getBagQuantity() {
		if(getTotalAmount() != null && MeasureUnit.DISCRETE_UNITS.contains(getTotalAmount().getMeasureUnit()) && getBagsInBox() > 1) {
			return getTotalAmount()
					.getAmount()
					.multiply(getWeightCoefficient(), MathContext.DECIMAL64)
					.multiply(BigDecimal.valueOf(getBagsInBox()))
					.setScale(MeasureUnit.SCALE, RoundingMode.HALF_DOWN);
		}
		else {
			return null;
		}
	}
	
	public BigDecimal getBoxQuantity() {
		if(getTotalAmount() != null && MeasureUnit.DISCRETE_UNITS.contains(getTotalAmount().getMeasureUnit())) {
			return getTotalAmount()
					.getAmount()
					.multiply(getWeightCoefficient(), MathContext.DECIMAL64)
					.setScale(MeasureUnit.SCALE, RoundingMode.HALF_DOWN);
		}
		else {
			return null;
		}
	}
	
	public BigDecimal getWeightInLbs() {
		if(getTotalAmount() == null) {
			return null;
		}
		AmountWithUnit weight;
		if(MeasureUnit.NONE == getItem().getUnit().getMeasureUnit() && MeasureUnit.WEIGHT_UNITS.contains(getTotalAmount().getMeasureUnit())) {
			weight = getTotalAmount();
		}
		else if(MeasureUnit.WEIGHT_UNITS.contains(getItem().getUnit().getMeasureUnit())) {
			weight = new AmountWithUnit(
					getTotalAmount().getAmount()
					.multiply(getItem().getUnit().getAmount(), MathContext.DECIMAL64), 
					getItem().getUnit().getMeasureUnit());
		}
		else {
			return null;			
		}
		
		if(weight.getMeasureUnit() != MeasureUnit.LBS) {
			weight = weight.convert(MeasureUnit.LBS);
		}
				
		return weight.getAmount()
				.multiply(getWeightCoefficient(), MathContext.DECIMAL64)
				.setScale(MeasureUnit.SCALE, RoundingMode.HALF_DOWN);
	}
	
	public String getType() {
		if(isWhole()) {
			return "WHOLE";
		}
		else {
			return "H&P";
		}
	}
	
	public ItemGroup getItemGroup() {
		return this.item.getGroup();
	}

}