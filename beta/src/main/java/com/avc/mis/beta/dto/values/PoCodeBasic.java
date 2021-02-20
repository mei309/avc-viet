/**
 * 
 */
package com.avc.mis.beta.dto.values;

import com.avc.mis.beta.dto.ValueDTO;
import com.avc.mis.beta.entities.codes.BasePoCode;
import com.avc.mis.beta.entities.item.Item;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.NonFinal;

/**
 * DTO for PoCode containing id and the fields needed 
 * for presenting the po code/id with it's initial and suffix.
 * 
 * @author Zvi
 *
 */
@Value
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
public class PoCodeBasic extends ValueDTO {

	String code;
	String contractTypeCode;
//	Currency currency;
	String contractTypeSuffix;
	String supplierName;	
//	String display;
//	@NonFinal
//	Class<? extends BasePoCode> clazz;
	
	/**
	 * @param id
	 * @param contractTypeCode
	 * @param contractTypeSuffix
	 * @param supplierName
	 */
	public PoCodeBasic(Integer id, String code,
			String contractTypeCode, String contractTypeSuffix, String supplierName
			) {
		super(id);
		this.code = code;
		this.contractTypeCode = contractTypeCode;
		this.supplierName = supplierName;
//		this.currency = currency;
		this.contractTypeSuffix = contractTypeSuffix != null ? contractTypeSuffix : "";
//		this.display = display;
	}	
	
//	public PoCodeBasic(Integer id, String code,
//			String contractTypeCode, String contractTypeSuffix, String supplierName,
//			String display, Class<? extends BasePoCode> clazz) {
//		super(id);
//		this.code = code;
//		this.contractTypeCode = contractTypeCode;
//		this.supplierName = supplierName;
//		this.contractTypeSuffix = contractTypeSuffix != null ? contractTypeSuffix : "";
//		this.display = display;
//		this.clazz = clazz;
//	}	
	
	/**
	 * @param poCode
	 */
	public PoCodeBasic(BasePoCode poCode) {
		super(poCode.getId());
		this.code = poCode.getCode();
		this.contractTypeCode = poCode.getContractType() != null ? poCode.getContractType().getCode(): null;
		this.supplierName = poCode.getSupplier() != null ? poCode.getSupplier().getName(): null;
//		this.currency = poCode.getContractType() != null ? poCode.getContractType().getCurrency(): null;
		this.contractTypeSuffix = poCode.getContractType() != null ? poCode.getContractType().getSuffix(): "";
//		this.display = poCode.getDisplay();
	}
	
	/**
	 * @return a string representing full PO code. e.g. VAT-900001, PO-900001V
	 */
	public String getValue() {	
//		if(this.display != null) {
//			return this.display;
//		}
		return String.format("%s-%s%s", this.contractTypeCode, this.getCode(), this.contractTypeSuffix);
	}
		
}
