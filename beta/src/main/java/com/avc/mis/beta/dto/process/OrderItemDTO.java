/**
 * 
 */
package com.avc.mis.beta.dto.process;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Optional;

import com.avc.mis.beta.dto.ProcessDTO;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.process.OrderItem;
import com.avc.mis.beta.entities.values.Item;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @author Zvi
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class OrderItemDTO extends ProcessDTO {

//	@EqualsAndHashCode.Exclude // for testing 
//	private Integer poId; //perhaps not needed, and if yes maybe get the whole PoCode
	private Item item;
	private MeasureUnit measureUnit;
	private BigDecimal numberUnits;
	private Currency currency;
	private BigDecimal unitPrice;
	private LocalDate deliveryDate;
	private String defects;
	private String remarks;
	
	
	public OrderItemDTO(Integer id, Integer version, Item item, 
			MeasureUnit measureUnit, BigDecimal numberUnits, Currency currency, BigDecimal unitPrice,
			LocalDate deliveryDate, String defects, String remarks) {
		super(id, version);
//		this.poId = poId;
		this.item = item;
		this.measureUnit = measureUnit;
		this.numberUnits = numberUnits;
		this.currency = currency;
		this.unitPrice = unitPrice;
		this.deliveryDate = deliveryDate;
		this.defects = defects;
		this.remarks = remarks;
	}
	
	public OrderItemDTO(@NonNull OrderItem orderItem) {
		super(orderItem.getId(), orderItem.getVersion());
//		this.poId = orderItem.getPo().getId();
		this.item = orderItem.getItem();
		this.measureUnit = orderItem.getMeasureUnit();
		this.numberUnits = orderItem.getNumberUnits();
		this.currency = orderItem.getCurrency();
		this.unitPrice = orderItem.getUnitPrice();
		this.deliveryDate = orderItem.getDeliveryDate();
		this.defects = orderItem.getDefects();
		this.remarks = orderItem.getRemarks();
	}
	
	public String getCurrency() {
		return Optional.ofNullable(this.currency).map(c -> c.getCurrencyCode()).orElse(null);
	}
}
