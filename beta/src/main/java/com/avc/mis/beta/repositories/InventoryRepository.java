/**
 * 
 */
package com.avc.mis.beta.repositories;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.Query;

import com.avc.mis.beta.dto.basic.PoCodeBasic;
import com.avc.mis.beta.dto.query.StorageBalance;
import com.avc.mis.beta.dto.report.ItemAmount;
import com.avc.mis.beta.dto.values.BasicValueEntity;
import com.avc.mis.beta.dto.view.ProcessItemInventory;
import com.avc.mis.beta.dto.view.ProcessItemInventoryRow;
import com.avc.mis.beta.dto.view.StorageInventoryRow;
import com.avc.mis.beta.entities.codes.PoCode;
import com.avc.mis.beta.entities.enums.ProductionFunctionality;
import com.avc.mis.beta.entities.item.Item;
import com.avc.mis.beta.entities.item.ItemGroup;
import com.avc.mis.beta.entities.item.ProductionUse;
import com.avc.mis.beta.service.report.row.ReceiptInventoryRow;

/**
 * @author Zvi
 *
 */
public interface InventoryRepository extends BaseRepository<PoCode> {

	/**
	 * AVAILABLE STORAGE FOR USE - PRODUCT OF FINAL PROCESS NOT USED BY ANOTHER PROCESS (EVEN NOT FINAL)
	 */
	@Query("select new com.avc.mis.beta.dto.view.StorageInventoryRow( "
			+ "sf.id, sf.version, sf.ordinal, "
			+ "pi.id, "
			+ "sf.unitAmount, sf.numberUnits, "
//			+ "sf.accessWeight, "
			+ "sto.id, sto.value, "
			+ "SUM("
				+ "(CASE "
					+ "WHEN (ui IS NOT null AND used_lc.processStatus <> com.avc.mis.beta.entities.enums.ProcessStatus.CANCELLED) "
						+ "THEN ui.numberUnits "
					+ "ELSE 0 "
				+ "END)"
			+ " ) AS total_used, "
			+ "(sf.unitAmount * uom.multiplicand / uom.divisor) "
				+ " * item_unit.amount "
				+ " * (sf.numberUnits - SUM("
					+ "(CASE "
						+ "WHEN (ui IS NOT null AND used_lc.processStatus <> com.avc.mis.beta.entities.enums.ProcessStatus.CANCELLED) "
							+ "THEN ui.numberUnits "
						+ "ELSE 0 "
					+ "END))) AS balance, "
			+ "(CASE "
			+ "WHEN type(item) = com.avc.mis.beta.entities.item.PackedItem THEN item_unit.measureUnit "
				+ "ELSE item.measureUnit "
			+ "END)) "
		+ "from ProcessItem pi "
			+ "join pi.item item "
				+ "join item.unit item_unit "
			+ "join UOM uom "
				+ "on uom.fromUnit = pi.measureUnit and uom.toUnit = item.measureUnit "
			+ "join pi.process p "
				+ "join p.lifeCycle lc "
			+ "join pi.allStorages sf "
				+ "join sf.group sf_group "
					+ "join sf_group.process sf_p "
						+ "left join sf_p.productionLine sf_p_line "
						+ "join sf_p.lifeCycle sf_lc "
				+ "left join sf.warehouseLocation sto "
				+ "left join sf.usedItems ui "
					+ "left join ui.group used_g "
						+ "left join used_g.process used_p "
							+ "left join used_p.lifeCycle used_lc "
		+ "where lc.processStatus = com.avc.mis.beta.entities.enums.ProcessStatus.FINAL "
			+ "and sf_lc.processStatus = com.avc.mis.beta.entities.enums.ProcessStatus.FINAL "
			+ "and (item.itemGroup = :itemGroup or :itemGroup is null) "
			+ "and (:checkProductionUses = false or item.productionUse in :productionUses) "
			+ "and (:checkFunctionalities = false or sf_p_line.productionFunctionality in :functionalities) "
			+ "and (item.id = :itemId or :itemId is null) "
			+ "and (:checkExcludedProcessIds = false or sf_p.id not in :excludedProcessIds) "
			+ "and "
			+ "(:checkPoCodes = false "
			+ "or "
			+ "EXISTS "
					+ "(select po_code.id "
					+ " from pi.process p_2 "
						+ "left join p_2.poCode p_po_code "
						+ "left join p_2.weightedPos w_po "
							+ "left join w_po.poCode w_po_code "
						+ "join BasePoCode po_code "
							+ "on (po_code = p_po_code or po_code = w_po_code) "
					+ "where po_code.id in :poCodeIds)) "
		+ "group by sf "
		+ "having sf.numberUnits > "
			+ "SUM("
				+ "(CASE "
					+ "WHEN (ui IS NOT null AND used_lc.processStatus <> com.avc.mis.beta.entities.enums.ProcessStatus.CANCELLED) "
						+ "THEN ui.numberUnits "
					+ "ELSE 0 "
				+ "END)"
			+ " ) "
		+ "order by p.recordedTime, pi.ordinal, sf.ordinal ")
	List<StorageInventoryRow> findAvailableInventoryByStorage(
			boolean checkProductionUses, ProductionUse[] productionUses, 
			boolean checkFunctionalities, ProductionFunctionality[] functionalities,
			ItemGroup itemGroup, Integer itemId, 
			boolean checkPoCodes, Integer[] poCodeIds,
			boolean checkExcludedProcessIds, Integer[] excludedProcessIds);
	
	@Query("select new com.avc.mis.beta.dto.view.ProcessItemInventory( "
			+ "pi.id, "
			+ "item.id, item.value, item.measureUnit, item.itemGroup, item_unit.amount, item_unit.measureUnit, type(item), "
			+ "pi.measureUnit, "
			+ "po_code.id, po_code.code, t.code, t.suffix, s.name, "
			+ "function('GROUP_CONCAT', function('DISTINCT', concat(t.code, '-', po_code.code, coalesce(t.suffix, '')))), "
			+ "function('GROUP_CONCAT', function('DISTINCT', s.name)), "
			+ "p.recordedTime, r.recordedTime, pi.tableView) "
		+ "from ProcessItem pi "
			+ "left join ReceiptItem ri "
				+ "on pi = ri "
			+ "join pi.item item "
				+ "join item.unit item_unit "
			+ "join pi.process p "
				+ "left join p.poCode p_po_code "
					+ "left join p.weightedPos w_po "
						+ "left join w_po.poCode w_po_code "
					+ "join BasePoCode po_code "
						+ "on (po_code = p_po_code or po_code = w_po_code) "
						+ "join po_code.contractType t "
						+ "join po_code.supplier s "
					+ "join Receipt r "
						+ "on r.poCode = po_code "
							+ "and (ri is null or ri.process = r) "
						+ "join r.lifeCycle receipt_lc "
		+ "where pi.id in :processItemIds "
		+ "group by pi "
		+ "order by p.recordedTime, pi.ordinal ")
	List<ProcessItemInventory> findProcessItemInventory(Set<Integer> processItemIds);

	/**
	 * INVENTORY FOR FINAL REPORT
	 */
	@Query("select new com.avc.mis.beta.dto.report.ItemAmount( "
			+ "item.id, item.value, item.measureUnit, item.itemGroup, item.productionUse, "
			+ "item_unit.amount, item_unit.measureUnit, type(item), "
			+ "SUM((sf.unitAmount * uom.multiplicand / uom.divisor) "
				+ " * "
				+ "(CASE "
					+ "WHEN ui is null THEN sf.numberUnits "
					+ "WHEN used_lc.processStatus = com.avc.mis.beta.entities.enums.ProcessStatus.FINAL "
						+ "THEN (sf.numberUnits / size(sf.usedItems) - ui.numberUnits) "
					+ "ELSE (sf.numberUnits / size(sf.usedItems)) "
				+ "END) "
			+ " ) AS balance, "
			+ "coalesce(w_po.weight, 1)) "
		+ "from ProcessItem pi "
			+ "left join ReceiptItem ri "
				+ "on pi = ri "
			+ "join pi.item item "
				+ "join item.unit item_unit "
			+ "join UOM uom "
				+ "on uom.fromUnit = pi.measureUnit and uom.toUnit = item.measureUnit "
			+ "join pi.process p "
				+ "join p.lifeCycle lc "
				+ "left join p.poCode p_po_code "
				+ "left join p.weightedPos w_po "
					+ "left join w_po.poCode w_po_code "
				+ "join BasePoCode po_code "
					+ "on (po_code = p_po_code or po_code = w_po_code) "
				+ "join Receipt r "
					+ "on r.poCode = po_code "
						+ "and (ri is null or ri.process = r) "
					+ "join r.lifeCycle receipt_lc "
			+ "join pi.allStorages sf "
				+ "join sf.group sf_group "
					+ "join sf_group.process sf_p "
						+ "join sf_p.lifeCycle sf_lc "
				+ "left join sf.usedItems ui "
					+ "left join ui.group used_g "
						+ "left join used_g.process used_p "
							+ "left join used_p.lifeCycle used_lc "
		+ "where lc.processStatus = com.avc.mis.beta.entities.enums.ProcessStatus.FINAL "
			+ "and receipt_lc.processStatus = com.avc.mis.beta.entities.enums.ProcessStatus.FINAL "
			+ "and sf_lc.processStatus = com.avc.mis.beta.entities.enums.ProcessStatus.FINAL "
			+ "and (:checkProductionUses = false or item.productionUse in :productionUses) "
			+ "and (item.itemGroup = :itemGroup or :itemGroup is null) "
			+ "and (item.id = :itemId or :itemId is null) "
			+ "and (po_code.id = :poCodeId or :poCodeId is null) "
			+ "and"
				+ "(sf.numberUnits > "
					+ "coalesce("
						+ "(select sum(usedStorage.numberUnits) "
						+ " from sf.usedItems usedStorage "
							+ "join usedStorage.group usedGroup "
								+ "join usedGroup.process usedProcess "
									+ "join usedProcess.lifeCycle usedLc "
						+ "where usedLc.processStatus = com.avc.mis.beta.entities.enums.ProcessStatus.FINAL) "
					+ ", 0)"
				+ ") "
		+ "group by item "
		+ ", w_po.weight "
//		+ "order by r.recordedTime, p.recordedTime " 
		+ "")
	List<ItemAmount> findInventoryItemAmounts(boolean checkProductionUses, ProductionUse[] productionUses, ItemGroup itemGroup, Integer itemId, Integer poCodeId);


	
	/**
	 * LIST OF INVENTORY ITEMS FOR REPORT	 
	 */
	@Query("select new com.avc.mis.beta.dto.view.ProcessItemInventoryRow( "
			+ "pi.id, "
			+ "item.id, item.value, item.measureUnit, item.itemGroup, item.productionUse, item_unit.amount, item_unit.measureUnit, type(item), "
			+ "po_code.id, po_code.code, t.code, t.suffix, s.name, "
			+ "p.recordedTime, r.recordedTime, "
			+ "coalesce(w_po.weight, 1), "
			+ "SUM((sf.unitAmount * uom.multiplicand / uom.divisor) "
//				+ " * item_unit.amount "
				+ " * "
				+ "(CASE "
					+ "WHEN ui is null THEN sf.numberUnits "
					+ "WHEN used_lc.processStatus = com.avc.mis.beta.entities.enums.ProcessStatus.FINAL "
						+ "THEN (sf.numberUnits / size(sf.usedItems) - ui.numberUnits) "
					+ "ELSE (sf.numberUnits / size(sf.usedItems)) "
				+ "END) "
//				+ " - "
//				+ "(CASE "
//					+ "WHEN ui is null THEN coalesce(sf.accessWeight, 0) "
//						+ "ELSE (coalesce(sf.accessWeight, 0) / size(sf.usedItems)) "
//				+ "END)"
			+ " ) AS balance, "
//			+ "(CASE type(item) "
//				+ "WHEN com.avc.mis.beta.entities.item.PackedItem THEN item_unit.measureUnit "
//				+ "ELSE item.measureUnit "
//			+ "END), "
			+ "function('GROUP_CONCAT', function('DISTINCT', sto.value))) "
		+ "from ProcessItem pi "
			+ "left join ReceiptItem ri "
				+ "on pi = ri "
			+ "join pi.item item "
				+ "join item.unit item_unit "
			+ "join UOM uom "
				+ "on uom.fromUnit = pi.measureUnit and uom.toUnit = item.measureUnit "
			+ "join pi.process p "
				+ "join p.lifeCycle lc "
				+ "left join p.poCode p_po_code "
				+ "left join p.weightedPos w_po "
					+ "left join w_po.poCode w_po_code "
					+ "join BasePoCode po_code "
						+ "on (po_code = p_po_code or po_code = w_po_code) "
						+ "join po_code.contractType t "
						+ "join po_code.supplier s "
					+ "join Receipt r "
						+ "on r.poCode = po_code "
							+ "and (ri is null or ri.process = r) "
						+ "join r.lifeCycle receipt_lc "
			+ "join pi.allStorages sf "
				+ "join sf.group sf_group "
					+ "join sf_group.process sf_p "
						+ "join sf_p.lifeCycle sf_lc "
				+ "left join sf.warehouseLocation sto "
				+ "left join sf.usedItems ui "
					+ "left join ui.group used_g "
						+ "left join used_g.process used_p "
							+ "left join used_p.lifeCycle used_lc "
		+ "where lc.processStatus = com.avc.mis.beta.entities.enums.ProcessStatus.FINAL "
			+ "and receipt_lc.processStatus = com.avc.mis.beta.entities.enums.ProcessStatus.FINAL "
			+ "and sf_lc.processStatus = com.avc.mis.beta.entities.enums.ProcessStatus.FINAL "
			+ "and (:checkProductionUses = false or item.productionUse in :productionUses) "
			+ "and (item.itemGroup = :itemGroup or :itemGroup is null) "
			+ "and (item.id = :itemId or :itemId is null) "
			+ "and (po_code.id = :poCodeId or :poCodeId is null) "
			+ "and"
				+ "(sf.numberUnits > "
					+ "coalesce("
						+ "(select sum(usedStorage.numberUnits) "
						+ " from sf.usedItems usedStorage "
							+ "join usedStorage.group usedGroup "
								+ "join usedGroup.process usedProcess "
									+ "join usedProcess.lifeCycle usedLc "
						+ "where usedLc.processStatus = com.avc.mis.beta.entities.enums.ProcessStatus.FINAL) "
					+ ", 0)"
				+ ") "
		+ "group by pi, w_po "
		+ "order by r.recordedTime, p.recordedTime " 
		+ "")
	List<ProcessItemInventoryRow> findInventoryProcessItemRows(
			boolean checkProductionUses, ProductionUse[] productionUses, ItemGroup itemGroup, Integer itemId, Integer poCodeId);

	/**
	 * ITEMS THAT HAVE AVAILABLE INVENTORY
	 */
	@Query("select new com.avc.mis.beta.dto.values.BasicValueEntity(item.id, item.value)  "
			+ "from ProcessItem pi "
				+ "join pi.item item "
				+ "join pi.process p "
					+ "left join p.poCode p_po_code "
					+ "left join p.weightedPos w_po "
						+ "left join w_po.poCode w_po_code "
					+ "join BasePoCode po_code "
						+ "on (po_code = p_po_code or po_code = w_po_code) "
					+ "join p.lifeCycle lc "
				+ "join pi.allStorages sf "
					+ "join sf.group sf_group "
						+ "join sf_group.process sf_p "
							+ "left join sf_p.productionLine sf_p_line "
							+ "join sf_p.lifeCycle sf_lc "
					+ "left join sf.usedItems ui "
						+ "left join ui.group used_g "
							+ "left join used_g.process used_p "
								+ "left join used_p.lifeCycle used_lc "
			+ "where lc.processStatus = com.avc.mis.beta.entities.enums.ProcessStatus.FINAL "
				+ "and sf_lc.processStatus = com.avc.mis.beta.entities.enums.ProcessStatus.FINAL "
				+ "and (item.itemGroup = :itemGroup or :itemGroup is null)  "
				+ "and (:checkProductionUses = false or item.productionUse in :productionUses)  "
				+ "and (:checkFunctionalities = false or sf_p_line.productionFunctionality in :functionalities) "
				+ "and (item.id = :itemId or :itemId is null)  "
				+ "and (po_code.id = :poCodeId or :poCodeId is null) "
			+ "group by sf.id, sf.numberUnits "
			+ "having sf.numberUnits > "
				+ "SUM("
					+ "(CASE "
						+ "WHEN (ui IS NOT null AND used_lc.processStatus <> com.avc.mis.beta.entities.enums.ProcessStatus.CANCELLED) "
							+ "THEN ui.numberUnits "
						+ "ELSE 0 "
					+ "END)"
				+ " ) ")
		Set<BasicValueEntity<Item>> findAvailableInventoryItemsByType(
				boolean checkProductionUses, ProductionUse[] productionUses, 
				boolean checkFunctionalities, ProductionFunctionality[] functionalities,
				ItemGroup itemGroup, Integer itemId,
				Integer poCodeId);
	
	/**
	 * PO CODES THAT HAVE AVAILABLE INVENTORY
	 * Gets set of All PoCodes that have item/s currently in available inventory 
	 * and contain the given item and belong to given supply group.
	 * Items are considered available inventory if the producing process status is final 
	 * and it's not completely used by another using process where the using process isn't cancelled.
	 * @param supplyGroup constrain to only this supply group, if null than any.
	 * @param itemId constrain to only this item, if null than any.
	 * @return Set of PoCodeBasic
	 */
	@Query("select new com.avc.mis.beta.dto.basic.PoCodeBasic("
			+ "po_code.id, po_code.code, t.code, t.suffix, s.name) "
		+ "from ProcessItem pi "
			+ "join pi.item item "
			+ "join pi.process p "
				+ "left join p.poCode p_po_code "
				+ "left join p.weightedPos w_po "
					+ "left join w_po.poCode w_po_code "
				+ "join BasePoCode po_code "
					+ "on (po_code = p_po_code or po_code = w_po_code) "
					+ "join po_code.contractType t "
					+ "join po_code.supplier s "
				+ "join p.lifeCycle lc "
			+ "join pi.allStorages sf "
				+ "join sf.group sf_group "
					+ "join sf_group.process sf_p "
						+ "left join sf_p.productionLine sf_p_line "
						+ "join sf_p.lifeCycle sf_lc "
				+ "left join sf.usedItems ui "
					+ "left join ui.group used_g "
						+ "left join used_g.process used_p "
							+ "left join used_p.lifeCycle used_lc "
		+ "where lc.processStatus = com.avc.mis.beta.entities.enums.ProcessStatus.FINAL "
			+ "and sf_lc.processStatus = com.avc.mis.beta.entities.enums.ProcessStatus.FINAL "
			+ "and (item.itemGroup = :itemGroup or :itemGroup is null)  "
			+ "and (:checkProductionUses = false or item.productionUse in :productionUses)  "
			+ "and (:checkFunctionalities = false or sf_p_line.productionFunctionality in :functionalities) "
			+ "and (item.id = :itemId or :itemId is null)  "
		+ "group by sf.id, sf.numberUnits, po_code.id "
//		+ "having (sf.numberUnits > sum(coalesce(ui.numberUsedUnits, 0))) "
		+ "having sf.numberUnits > "
			+ "SUM("
				+ "(CASE "
					+ "WHEN (ui IS NOT null AND used_lc.processStatus <> com.avc.mis.beta.entities.enums.ProcessStatus.CANCELLED) "
						+ "THEN ui.numberUnits "
					+ "ELSE 0 "
				+ "END)"
			+ " ) ")
	Set<PoCodeBasic> findAvailableInventoryPoCodeByType(
			boolean checkProductionUses, ProductionUse[] productionUses, 
			boolean checkFunctionalities, ProductionFunctionality[] functionalities,
			ItemGroup itemGroup, Integer itemId);
		
	
	/**
	 * Gets the storage balance for storages used by the given process.
	 * @param processId id of the process
	 * @return Stream of StorageBalance
	 */
	@Query("select new com.avc.mis.beta.dto.query.StorageBalance("
			+ "s.id, s.numberUnits, "
			+ "SUM("
			+ "(CASE "
				+ "WHEN (used_lc.processStatus <> com.avc.mis.beta.entities.enums.ProcessStatus.CANCELLED) "
					+ "THEN ui.numberUnits "
				+ "ELSE 0 "
			+ "END))) "
			+ "from TransactionProcess p "
				+ "join p.usedItemGroups grp "
					+ "join grp.usedItems i "
						+ "join i.storage s "
							+ "join s.usedItems ui "
								+ "join ui.group used_g "
									+ "join used_g.process used_p "
										+ "join used_p.lifeCycle used_lc "
			+ "where p.id = :processId "
			+ "group by grp, i, s ")
	List<StorageBalance> findUsedStorageBalances(Integer processId);
	
	@Query("select new com.avc.mis.beta.dto.query.StorageBalance("
			+ "s.id, s.numberUnits, "
			+ "SUM("
			+ "(CASE "
				+ "WHEN (used_lc.processStatus <> com.avc.mis.beta.entities.enums.ProcessStatus.CANCELLED) "
					+ "THEN ui.numberUnits "
				+ "ELSE 0 "
			+ "END))) "
			+ "from ProcessWithProduct p "
				+ "join p.processItems pi "
					+ "join pi.storageForms s "
						+ "join s.usedItems ui "
							+ "join ui.group used_g "
								+ "join used_g.process used_p "
									+ "join used_p.lifeCycle used_lc "
			+ "where p.id = :processId "
			+ "group by pi, s ")
	Stream<StorageBalance> findProducedStorageBalances(Integer processId);

	@Query("select new com.avc.mis.beta.dto.query.StorageBalance("
			+ "s.id, s.numberUnits, "
			+ "SUM("
			+ "(CASE "
				+ "WHEN (used_lc.processStatus <> com.avc.mis.beta.entities.enums.ProcessStatus.CANCELLED) "
					+ "THEN ui.numberUnits "
				+ "ELSE 0 "
			+ "END))) "
			+ "from StorageRelocation p "
				+ "join p.storageMovesGroups grp "
					+ "join grp.storageMoves i "
						+ "join i.storage s "
							+ "join s.usedItems ui "
								+ "join ui.group used_g "
									+ "join used_g.process used_p "
										+ "join used_p.lifeCycle used_lc "
			+ "where p.id = :processId "
			+ "group by grp, i, s ")
	List<StorageBalance> findRelocationUseBalances(Integer processId);
	
	@Query("select new com.avc.mis.beta.dto.query.StorageBalance("
			+ "i.id, i.numberUnits, "
			+ "SUM("
			+ "(CASE "
				+ "WHEN (used_lc.processStatus <> com.avc.mis.beta.entities.enums.ProcessStatus.CANCELLED) "
					+ "THEN ui.numberUnits "
				+ "ELSE 0 "
			+ "END))) "
			+ "from StorageRelocation p "
				+ "join p.storageMovesGroups g "
					+ "join g.storageMoves i "
							+ "left join i.usedItems ui "
								+ "join ui.group used_g "
									+ "join used_g.process used_p "
										+ "join used_p.lifeCycle used_lc "
			+ "where p.id = :processId "
			+ "group by g, i "
//			+ ", s"
			+ "")
	Stream<StorageBalance> findStorageMoveBalances(Integer processId);

	/**
	 * LIST OF INVENTORY POS FOR REPORT, including price and bags. (Used for raw material inventory)
	 */
	@Query("select new com.avc.mis.beta.service.report.row.ReceiptInventoryRow( "
			+ "s.name, "
			+ "item.value, "
			+ "concat(t.code, '-', po_code.code, coalesce(t.suffix, '')), "
			+ "r.recordedTime, "
			+ "function('GROUP_CONCAT', "
				+ "concat( "
					+ "cast((CASE "
						+ "WHEN ui is null THEN sf.numberUnits "
						+ "WHEN used_lc.processStatus = com.avc.mis.beta.entities.enums.ProcessStatus.FINAL "
							+ "THEN (sf.numberUnits / size(sf.usedItems) - ui.numberUnits) "
						+ "ELSE (sf.numberUnits / size(sf.usedItems)) "
					+ "END) as string)+0, "
					+ "'x', "
					+ "cast(sf.unitAmount as string)+0, "
					+ "ri.measureUnit)), "
			+ "SUM((sf.unitAmount * uom.multiplicand / uom.divisor) "
				+ " * "
				+ "(CASE "
					+ "WHEN ui is null THEN sf.numberUnits "
					+ "WHEN used_lc.processStatus = com.avc.mis.beta.entities.enums.ProcessStatus.FINAL "
						+ "THEN (sf.numberUnits / size(sf.usedItems) - ui.numberUnits) "
					+ "ELSE (sf.numberUnits / size(sf.usedItems)) "
				+ "END) "
			+ " ) AS balance, rou.measureUnit, "
			+ "function('GROUP_CONCAT', function('DISTINCT', sto.value)), "
			+ "price, t.currency) "
		+ "from ReceiptItem ri "
			+ "join ri.receivedOrderUnits rou "
				+ "join UOM uom "
					+ "on uom.fromUnit = ri.measureUnit and uom.toUnit = rou.measureUnit "
			+ "left join ri.unitPrice price "
			+ "join ri.item item "
			+ "join ri.process r "
				+ "join r.lifeCycle receipt_lc "
				+ "join r.poCode po_code "
					+ "join po_code.contractType t "
					+ "join po_code.supplier s "
			+ "join ri.allStorages sf "
				+ "join sf.group sf_group "
					+ "join sf_group.process sf_p "
						+ "join sf_p.lifeCycle sf_lc "
				+ "left join sf.warehouseLocation sto "
				+ "left join sf.usedItems ui "
					+ "left join ui.group used_g "
						+ "left join used_g.process used_p "
							+ "left join used_p.lifeCycle used_lc "
		+ "where receipt_lc.processStatus = com.avc.mis.beta.entities.enums.ProcessStatus.FINAL "
			+ "and sf_lc.processStatus = com.avc.mis.beta.entities.enums.ProcessStatus.FINAL "
			+ "and (:checkProductionUses = false or item.productionUse in :productionUses) "
			+ "and (item.itemGroup = :itemGroup or :itemGroup is null) "
			+ "and (coalesce(:startDateTime, :endDateTime) is null "
				+ "or r.recordedTime between :startDateTime and :endDateTime) "
			+ "and"
				+ "(sf.numberUnits > "
					+ "coalesce("
						+ "(select sum(usedStorage.numberUnits) "
						+ " from sf.usedItems usedStorage "
							+ "join usedStorage.group usedGroup "
								+ "join usedGroup.process usedProcess "
									+ "join usedProcess.lifeCycle usedLc "
						+ "where usedLc.processStatus = com.avc.mis.beta.entities.enums.ProcessStatus.FINAL) "
					+ ", 0)"
				+ ") "
		+ "group by ri "
		+ "order by r.recordedTime " 
		+ "")
	List<ReceiptInventoryRow> findReceiptInventoryRows(boolean checkProductionUses, ProductionUse[] productionUses, ItemGroup itemGroup,
			OffsetDateTime startDateTime, OffsetDateTime endDateTime);




}
