/**
 * 
 */
package com.avc.mis.beta.repositories;

import java.util.stream.Stream;

import org.springframework.data.jpa.repository.Query;

import com.avc.mis.beta.dto.view.ProductionProcessWithItemAmount;
import com.avc.mis.beta.entities.process.PoProcess;

/**
 * Spring repository for accessing all notification information and requirements of production processes.
 * 
 * @author Zvi
 *
 */
public interface PoProcessRepository<T extends PoProcess> extends ProcessRepository<T> {
	
	@Query("select new com.avc.mis.beta.dto.view.ProductionProcessWithItemAmount("
			+ "p.id, item.id, item.value, item.defaultMeasureUnit, item_unit.amount, item_unit.measureUnit, type(item), "
			+ "SUM((count_amount.amount - coalesce(item_count.containerWeight, 0)) * uom.multiplicand / uom.divisor) - coalesce(item_count.accessWeight, 0)) "
		+ "from PoProcess p "
			+ "join p.itemCounts item_count "
				+ "join item_count.item item "
					+ "join item.unit item_unit "
				+ "join item_count.amounts count_amount "
					+ "join UOM uom "
						+ "on uom.fromUnit = item_count.measureUnit and uom.toUnit = item.defaultMeasureUnit "
			+ "join p.processType pt "
		+ "where "
//			+ "pt.processName = :processName "
			+ "p.id in :processIds "
		+ "group by p, item_count ")
	Stream<ProductionProcessWithItemAmount> findAllItemsCountsByProcessIds(int[] processIds);


}
