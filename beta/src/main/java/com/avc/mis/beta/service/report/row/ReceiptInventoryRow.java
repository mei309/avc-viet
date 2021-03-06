/**
 * 
 */
package com.avc.mis.beta.service.report.row;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.avc.mis.beta.dto.BasicDTO;
import com.avc.mis.beta.entities.embeddable.AmountWithCurrency;
import com.avc.mis.beta.entities.embeddable.AmountWithUnit;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.enums.ProcessStatus;
import com.avc.mis.beta.entities.enums.ProductionFunctionality;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zvi
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReceiptInventoryRow extends BasicDTO {

	private String supplier;
//	private CashewItemDTO cashewItem;
	private String productCompany;
	private String item;
	private boolean whole;
	private String poCode;
	private LocalDateTime receiptDate;
//	private LocalDateTime processDate;
	private ProductionFunctionality productionFunctionality;
	private String[] bags;
	private AmountWithUnit amount;
	private String[] warehouses;
	private AmountWithCurrency unitPrice;
	private Currency currency;
	private ProcessStatus status;

	private BigDecimal rawDefectsAndDamage;
		
	public ReceiptInventoryRow(Integer poCodeId, String supplier, String productCompany, 
			String item, boolean whole,
//			Integer itemId, String itemValue, 
//			String itemCode, String itemBrand, MeasureUnit itemMeasureUnit, ItemGroup itemGroup, ProductionUse itemProductionUse, 
//			AmountWithUnit itemUnit, Class<? extends Item> itemClazz, 
//			int itemNumBags, CashewGrade itemGrade, boolean itemWhole, boolean itemRoast, boolean itemToffee, SaltLevel itemSaltLevel, 
			String poCode, LocalDateTime receiptDate, 
//			LocalDateTime processDate, 
			ProductionFunctionality productionFunctionality, 
			String bags,
			BigDecimal amount, MeasureUnit measureUnit,
			String warehouses, 
			AmountWithCurrency unitPrice, Currency currency, ProcessStatus status) {
		super(poCodeId);
		this.supplier = supplier;
		this.productCompany = productCompany;
		this.item = item;
		this.whole = whole;
//		this.cashewItem = new CashewItemDTO(itemId, itemValue, itemCode, itemBrand, itemMeasureUnit, itemGroup, itemProductionUse, itemUnit, itemClazz, 
//				itemNumBags, itemGrade, itemWhole, itemRoast, itemToffee, itemSaltLevel);
		this.poCode = poCode;
		this.receiptDate = receiptDate;
//		this.processDate = processDate;
		this.productionFunctionality = productionFunctionality;
		if(bags != null) {
//			this.bags = Stream.of(bags.split(",")).toArray(String[]::new);
			this.bags = Stream.of(bags.split(","))
					.map(s -> {
						String[] array = s.split("x");						
						return new Object[] {Double.valueOf(array[0]), array[1]};
					})
					.collect(Collectors.groupingBy(array -> (String)array[1], LinkedHashMap::new, Collectors.summingDouble(array -> (Double)array[0])))
					.entrySet().stream().map(entry -> Math.round(entry.getValue()) + "x" + entry.getKey()).toArray(String[]::new);
		}
		else {
			this.bags = null;
		}
		this.amount = new AmountWithUnit(amount, measureUnit).setScale(MeasureUnit.SCALE);
//		this.amount.setScale(MeasureUnit.SCALE);
		if(warehouses != null) {
			this.warehouses = Stream.of(warehouses.split(",")).toArray(String[]::new);
		}
		else {
			this.warehouses = null;
		}
		this.unitPrice = unitPrice;
		this.currency = currency;
		this.status = status;
	} 
	
	public BigDecimal getWeightInLbs() {
		BigDecimal weightInLbs;
		if(getAmount().getMeasureUnit() == MeasureUnit.LBS) {
			weightInLbs = getAmount().getAmount();
		}
		
		try {
			weightInLbs = getAmount().convert(MeasureUnit.LBS).getAmount();
		} catch (UnsupportedOperationException e) {
			return null;
		}
		
		return weightInLbs.setScale(0, RoundingMode.HALF_DOWN);
	}
	
	public String getType() {
		if(isWhole()) {
			return "WHOLE";
		}
		else {
			return "H&P";
		}
	}
	
	public BigDecimal getBalance() {
		BigDecimal balance = getAmount().getAmount();
		if(balance.compareTo(BigDecimal.ZERO) == 0)
			return null;
		return balance;
	}
	
	public MeasureUnit getMeasureUnit() {
		return getAmount().getMeasureUnit();
	}
	
	public String[] getWarehouses() {
		if(getBalance() == null)
			return null;
		return warehouses;
	}
	
	public String[] getBags() {
		if(getBalance() == null)
			return null;
		return bags;
	}

}
