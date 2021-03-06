/**
 * 
 */
package com.avc.mis.beta.dto.report;

import java.util.List;

import com.avc.mis.beta.entities.embeddable.AmountWithUnit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Production report summary for final report.
 * @author zvi
 *
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
public class ProductReportLine extends ReportLine {

	private List<ItemAmount> productCount;

	private AmountWithUnit totalProductCount;	
	
	public void setProductCount(List<ItemAmount> productCount) {
		boolean empty = productCount == null || productCount.isEmpty();
		this.productCount = empty ? null : productCount;
		this.totalProductCount = empty ? null : ItemAmount.getTotalWeight(productCount);
	}
	


	


}
