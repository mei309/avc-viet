package com.avc.mis.beta.repositories;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.Query;

import com.avc.mis.beta.dto.process.StorageTransferDTO;
import com.avc.mis.beta.dto.query.ItemCountWithAmount;
import com.avc.mis.beta.dto.query.ItemTransactionDifference;
import com.avc.mis.beta.dto.view.ProductionProcessWithItemAmount;
import com.avc.mis.beta.entities.process.StorageTransfer;

public interface TransferRepository extends TransactionProcessRepository<StorageTransfer>{
	
	@Query("select new com.avc.mis.beta.dto.view.ProductionProcessWithItemAmount("
			+ "t.id, item.id, item.value, "
			+ "SUM((count_amount.amount - coalesce(item_count.containerWeight, 0)) * uom.multiplicand / uom.divisor) - coalesce(t.accessWeight, 0), "
			+ "item.measureUnit) "
		+ "from StorageTransfer t "
			+ "join t.itemCounts item_count "
				+ "join item_count.item item "
				+ "join item_count.amounts count_amount "
					+ "join UOM uom "
						+ "on uom.fromUnit = item_count.measureUnit and uom.toUnit = item.measureUnit "
		+ "group by t, item ")
	Stream<ProductionProcessWithItemAmount> findAllItemsCounts();

	@Query("select new com.avc.mis.beta.dto.process.StorageTransferDTO("
			+ "r.id, r.version, r.createdDate, p_user.username, "
			+ "po_code.code, t.code, t.suffix, s.id, s.version, s.name, "
			+ "pt.processName, p_line, "
			+ "r.recordedTime, r.startTime, r.endTime, r.duration, r.numOfWorkers, "
			+ "lc.processStatus, lc.editStatus, r.remarks, function('GROUP_CONCAT', concat(u.username, ':', approval.decision)), "
			+ "r.accessWeight) "
		+ "from StorageTransfer r "
			+ "join r.poCode po_code "
				+ "join po_code.contractType t "
				+ "join po_code.supplier s "
			+ "join r.processType pt "
			+ "left join r.createdBy p_user "
			+ "left join r.productionLine p_line "
			+ "join r.lifeCycle lc "
			+ "left join r.approvals approval "
				+ "left join approval.user u "
		+ "where r.id = :processId ")
	Optional<StorageTransferDTO> findTransferDTOByProcessId(int processId);

	@Query("select new com.avc.mis.beta.dto.query.ItemTransactionDifference("
				+ "usedItem.id, usedItem.value, "
				+ "SUM(used_unit.amount * used_sf.numberUnits * uom_used.multiplicand / uom_used.divisor), "
				+ "usedItem.measureUnit, "
				+ "SUM(producedUnit.amount * sf.numberUnits * uom_produced.multiplicand / uom_produced.divisor), "
				+ "usedItem.measureUnit) "
			+ "from TransactionProcess p "
				+ "join p.usedItemGroups grp "
					+ "join grp.usedItems ui "
						+ "join ui.storage used_sf "
							+ "join used_sf.processItem usedPi "
								+ "join usedPi.item usedItem "
								+ "join used_sf.unitAmount used_unit "
								+ "join UOM uom_used "
									+ "on uom_used.fromUnit = used_unit.measureUnit and uom_used.toUnit = usedItem.measureUnit "						
				+ "left join p.processItems pi on pi.item = usedItem "
//					+ "left join pi.item producedItem "
					+ "left join pi.storageForms sf "
						+ "left join sf.unitAmount producedUnit "
						+ "left join UOM uom_produced "
							+ "on uom_produced.fromUnit = pi.item.measureUnit and uom_produced.toUnit = usedItem.measureUnit "
			+ "where p.id = :processId "
//				+ "and (pi is null or usedItem = producedItem) "
			+ "group by usedItem ")
	List<ItemTransactionDifference> findTransferDifferences(Integer processId);

	@Query("select new com.avc.mis.beta.dto.query.ItemCountWithAmount( "
			+ " i.id, i.version, "
			+ "item.id, item.value, item.category, "
			+ "i.measureUnit, i.containerWeight,"
			+ "poCode.code, ct.code, ct.suffix, s.name, "
			+ "count_amount.id, count_amount.version, count_amount.ordinal, count_amount.amount) "
		+ "from ItemCount i "
			+ "join i.item item "
			+ "join i.process p "
				+ "join p.poCode poCode "
					+ "join poCode.contractType ct "
					+ "join poCode.supplier s "
			+ "join i.amounts count_amount "
		+ "where p.id = :processId ")
	List<ItemCountWithAmount> findItemCountWithAmount(int processId);

	
	//already in ProcessRepository
//	@Query("select new com.avc.mis.beta.dto.query.ProcessItemWithStorage( "
//			+ " i.id, i.version, item.id, item.value, "
//			+ "poCode.code, ct.code, s.name, "
//			+ "sf.id, sf.version, "
//			+ "unit.amount, unit.measureUnit, sf.numberUnits, "
//			+ "warehouseLocation.id, warehouseLocation.value, sf.remarks, type(sf), "
//			+ "i.description, i.remarks) "
//		+ "from ProcessItem i "
//			+ "join i.item item "
//			+ "join i.process p "
//				+ "join p.poCode poCode "
//					+ "join poCode.contractType ct "
//					+ "join poCode.supplier s "
//			+ "join i.storageForms sf "
//				+ "join sf.unitAmount unit "
//				+ "left join sf.warehouseLocation warehouseLocation "
//		+ "where p.id = :processId ")
//	List<ProcessItemWithStorage> findProcessItemWithStorage(int processId);

//	@Query("select new com.avc.mis.beta.dto.query.ProcessItemWithStorage( "
//		+ " i.id, i.version, item.id, item.value, "
//		+ "poCode.code, ct.code, s.name, "
//		+ "sf.id, sf.version, "
//		+ "unit.amount, unit.measureUnit, sf.numberUnits, "
//		+ "warehouseLocation.id, warehouseLocation.value, sf.remarks, type(sf), "
//		+ "i.description, i.remarks) "
//	+ "from ProcessItem i "
//		+ "join i.item item "
//		+ "join i.process p "
//			+ "join p.poCode poCode "
//				+ "join poCode.contractType ct "
//				+ "join poCode.supplier s "
//		+ "join i.storageForms sf "
//			+ "join sf.unitAmount unit "
//			+ "left join sf.warehouseLocation warehouseLocation "
//	+ "where poCode.code = :poCodeId ")
//	List<ProcessItemWithStorage> findProcessItemWithStorageByPoCode(Integer poCodeId);
//	
//	@Query("select new com.avc.mis.beta.dto.query.ProcessItemWithStorage( "
//		+ " i.id, i.version, item.id, item.value, "
//		+ "poCode.code, ct.code, s.name, "
//		+ "sf.id, sf.version, "
//		+ "unit.amount, unit.measureUnit, sf.numberUnits, "
//		+ "warehouseLocation.id, warehouseLocation.value, sf.remarks, type(sf), "
//		+ "i.description, i.remarks) "
//	+ "from ProcessItem i "
//		+ "join i.item item "
//		+ "join i.process p "
//			+ "join p.poCode poCode "
//				+ "join poCode.contractType ct "
//				+ "join poCode.supplier s "
//		+ "join i.storageForms sf "
//			+ "join sf.unitAmount unit "
//			+ "left join sf.warehouseLocation warehouseLocation "
//	+ "where item.id = :itemId ")
//	List<ProcessItemWithStorage> findProcessItemWithStorageByItem(Integer itemId);

}
