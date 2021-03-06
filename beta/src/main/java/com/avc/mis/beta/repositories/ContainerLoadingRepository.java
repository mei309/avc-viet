/**
 * 
 */
package com.avc.mis.beta.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;

import com.avc.mis.beta.dto.exportdoc.ContainerPoItemRow;
import com.avc.mis.beta.dto.exportdoc.ContainerPoItemStorageRow;
import com.avc.mis.beta.dto.exportdoc.ExportInfo;
import com.avc.mis.beta.dto.process.collectionItems.LoadedItemDTO;
import com.avc.mis.beta.dto.process.info.ContainerLoadingInfo;
import com.avc.mis.beta.dto.view.LoadingRow;
import com.avc.mis.beta.entities.enums.ItemGroup;
import com.avc.mis.beta.entities.enums.ProductionUse;
import com.avc.mis.beta.service.report.row.CashewExportReportRow;

/**
 * @author zvi
 *
 */
public interface ContainerLoadingRepository extends RelocationRepository {
	
	/**
	 * Gets the access loading information (over the general process information assumed to be fetched).
	 * @param processId id of loading process info to be fetched.
	 * @return ContainerLoadingInfo object that contains loading process information.
	 */
	@Query("select new com.avc.mis.beta.dto.process.info.ContainerLoadingInfo( "
			+ "sc.id, sc.code, port.id, port.value, port.code, "
			+ "arrival.id, arrival.version, cd.containerNumber, "
			+ "pc.id, pc.version, pc.name) "
		+ "from ContainerLoading r "
			+ "join r.arrival arrival "
				+ "join arrival.containerDetails cd "
				+ "left join arrival.productCompany pc "
			+ "join r.shipmentCode sc "
				+ "join sc.portOfDischarge port "
		+ "where r.id = :processId ")
	ContainerLoadingInfo findContainerLoadingInfo(int processId);

	/**
	 * Gets the join of loaded item, process and storage information for the given container loading process.
	 * @param processId id of the process
	 * @return List of LoadedItemWithStorage
	 */
	@Query("select new com.avc.mis.beta.dto.process.collectionItems.LoadedItemDTO( "
			+ " i.id, i.version, i.ordinal, "
			+ "item.id, item.value, item.productionUse, type(item), "
			+ "poCode.id, poCode.code, ct.code, ct.suffix, s.name, "
			+ "da.amount, da.measureUnit, "
			+ "i.description, i.remarks) "
		+ "from LoadedItem i "
			+ "join i.item item "
			+ "left join i.declaredAmount da "
			+ "join i.process p "
				+ "join p.poCode poCode "
					+ "join poCode.contractType ct "
					+ "join poCode.supplier s "
		+ "where p.id = :processId "
		+ "order by i.ordinal ")
	List<LoadedItemDTO> findLoadedItems(int processId);

	@Query("select new com.avc.mis.beta.dto.view.LoadingRow( "
			+ "p.id, "
			+ "function('GROUP_CONCAT', function('DISTINCT', po_code.id)), "
			+ "function('GROUP_CONCAT', function('DISTINCT', concat(t.code, '-', po_code.code, coalesce(t.suffix, '')))), "
			+ "function('GROUP_CONCAT', function('DISTINCT', s.name)), "
			+ "p.recordedTime, p.downtime, lc.processStatus, "
			+ "function('GROUP_CONCAT', function('DISTINCT', concat(u.username, ': ', approval.decision))), "
			+ "shipment_code.id, shipment_code.code, pod.code, pod.value, "
			+ "ship.eta, cont.containerNumber, cont.sealNumber, cont.containerType) "
		+ "from ContainerLoading p "
			+ "left join p.poCode p_po_code "
			+ "left join p.weightedPos w_po "
				+ "left join w_po.poCode w_po_code "
				+ "left join BasePoCode po_code "
					+ "on (po_code = p_po_code or po_code = w_po_code) "
					+ "left join po_code.contractType t "
					+ "left join po_code.supplier s "
			+ "join p.shipmentCode shipment_code "
				+ "join shipment_code.portOfDischarge pod "
			+ "join p.arrival cont_arrival "
				+ "join cont_arrival.containerDetails cont "
				+ "join cont_arrival.shipingDetails ship "
			+ "join p.processType pt "
			+ "join p.lifeCycle lc "
			+ "left join p.approvals approval "
				+ "left join approval.user u "
		+ "where "
			+ "(po_code.id = :poCodeId or :poCodeId is null) "
			+ "and ((:cancelled is true) or (lc.processStatus <> com.avc.mis.beta.entities.enums.ProcessStatus.CANCELLED)) "
			+ "and (:startTime is null or p.recordedTime >= :startTime) "
			+ "and (:endTime is null or p.recordedTime < :endTime) "
		+ "group by p "
		+ "order by p.recordedTime desc ")
	List<LoadingRow> findContainerLoadings(Integer poCodeId, boolean cancelled, LocalDateTime startTime, LocalDateTime endTime);

	@Query("select new com.avc.mis.beta.dto.exportdoc.ExportInfo( "
			+ "shipment_code.id, shipment_code.code, pod.code, pod.value, p.recordedTime) "
		+ "from ContainerLoading p "
				+ "join p.shipmentCode shipment_code "
					+ "join shipment_code.portOfDischarge pod "
		+ "where p.id = :processId ")
	Optional<ExportInfo> findInventoryExportDocById(int processId);

	@Query("select new com.avc.mis.beta.dto.exportdoc.ContainerPoItemRow( "
			+ "p.id, "
			+ "item.id, item.value, item.measureUnit, "
			+ "item_unit.amount, item_unit.measureUnit, type(item), "
			+ "function('GROUP_CONCAT', function('DISTINCT', concat(t.code, '-', po_code.code, coalesce(t.suffix, '')))), "
			+ "sum("
				+ "(coalesce(sf.unitAmount, 1) * sf.numberUnits) "
				+ " * coalesce(w_po.weight, 1) * uom.multiplicand / uom.divisor), "
			+ "item.measureUnit) "
		+ "from ContainerLoading p "
			+ "join p.storageMovesGroups g "
				+ "join g.storageMoves sf "
						+ "join sf.processItem pi "
							+ "join pi.process used_p "
								+ "left join used_p.poCode p_po_code "
								+ "left join used_p.weightedPos w_po "
									+ "left join w_po.poCode w_po_code "
								+ "left join BasePoCode po_code "
									+ "on (po_code = p_po_code or po_code = w_po_code) "
									+ "left join po_code.contractType t "
									+ "left join po_code.supplier s "
							+ "join pi.item item "
								+ "join item.unit item_unit "
							+ "join UOM uom "
								+ "on uom.fromUnit = pi.measureUnit and uom.toUnit = item.measureUnit "				
		+ "where p.id in :processIds "
			+ "and (po_code.id = :poCodeId or :poCodeId is null) "
		+ "group by p, item.id "
		+ "order by p")
	List<ContainerPoItemRow> findLoadedTotals(int[] processIds, Integer poCodeId);

	@Query("select new com.avc.mis.beta.dto.exportdoc.ContainerPoItemStorageRow( "
			+ "item.id, item.value, item.measureUnit, "
			+ "item_unit.amount, item_unit.measureUnit, type(item), "
			+ "function('GROUP_CONCAT', function('DISTINCT', concat(t.code, '-', po_code.code, coalesce(t.suffix, '')))), "
			+ "sf.unitAmount, pi.measureUnit, "
			+ "sum(sf.numberUnits * coalesce(w_po.weight, 1)), "
			+ "sum(coalesce(w_po.weight, 1) * "
				+ "(CASE "
					+ "WHEN item_unit.measureUnit <> com.avc.mis.beta.entities.enums.MeasureUnit.NONE or sf.unitAmount is not null "
						+ "THEN sf.numberUnits "
					+ "ELSE coalesce(sf.unitAmount, 1) "
				+ "END)"
			+ ")) "			
		+ "from ContainerLoading p "
			+ "join p.storageMovesGroups g "
				+ "join g.storageMoves sf "
						+ "join sf.processItem pi "
							+ "join pi.process used_p "
								+ "left join used_p.poCode p_po_code "
								+ "left join used_p.weightedPos w_po "
									+ "left join w_po.poCode w_po_code "
								+ "left join BasePoCode po_code "
									+ "on (po_code = p_po_code or po_code = w_po_code) "
									+ "left join po_code.contractType t "
									+ "left join po_code.supplier s "
							+ "join pi.item item "
								+ "join item.unit item_unit "
		+ "where p.id = :processId "
		+ "group by item.id, sf.unitAmount, pi.measureUnit ")
	List<ContainerPoItemStorageRow> findLoadedStorages(int processId);

	@Query("select new com.avc.mis.beta.service.report.row.CashewExportReportRow( "
			+ "item.id, item.value, item.measureUnit, item.itemGroup, item.productionUse, item.unit, type(item), item.brand, item.code, "
			+ "item.whole, item.roast, item.toffee, "
			+ "grade.id, grade.value, item.saltLevel, item.numBags, "
			+ "sum(sf.numberUnits * coalesce(w_po.weight, 1)), pi.measureUnit, "
			+ "sum(coalesce(w_po.weight, 1) * "
				+ "(CASE "
					+ "WHEN item_unit.measureUnit <> com.avc.mis.beta.entities.enums.MeasureUnit.NONE or sf.unitAmount is not null "
						+ "THEN sf.numberUnits "
					+ "ELSE coalesce(sf.unitAmount, 1) "
				+ "END)"
			+ "), "
			+ "concat(t.code, '-', po_code.code, coalesce(t.suffix, '')), "
			+ "p.recordedTime, "
			+ "shipment_code.id, shipment_code.code, pod.code, pod.value, "
			+ "ship.eta, cont.containerNumber, cont.sealNumber, cont.containerType, p.remarks) "
		+ "from ContainerLoading p "
			+ "join p.shipmentCode shipment_code "
				+ "join shipment_code.portOfDischarge pod "
			+ "join p.arrival cont_arrival "
				+ "join cont_arrival.containerDetails cont "
				+ "join cont_arrival.shipingDetails ship "
			+ "join p.lifeCycle lc "
			+ "join p.storageMovesGroups g "
				+ "join g.storageMoves sf "
						+ "join sf.processItem pi "
							+ "join pi.item item "
								+ "join item.unit item_unit "
								+ "left join item.grade grade "
							+ "join pi.process used_p "
								+ "left join used_p.poCode p_po_code "
								+ "left join used_p.weightedPos w_po "
									+ "left join w_po.poCode w_po_code "
								+ "left join BasePoCode po_code "
									+ "on (po_code = p_po_code or po_code = w_po_code) "
									+ "left join po_code.contractType t "
									+ "left join po_code.supplier s "
		+ "where lc.processStatus = com.avc.mis.beta.entities.enums.ProcessStatus.FINAL "
			+ "and (:checkProductionUses = false or item.productionUse in :productionUses) "
			+ "and (item.itemGroup = :itemGroup or :itemGroup is null) "
			+ "and (:startTime is null or p.recordedTime >= :startTime) "
			+ "and (:endTime is null or p.recordedTime < :endTime) "
		+ "group by p.id, item.itemGroup, item, po_code, pi.measureUnit "
		)	
	List<CashewExportReportRow> findCashewExportReportRows(
			boolean checkProductionUses, ProductionUse[] productionUses,
			ItemGroup itemGroup, 
			LocalDateTime startTime, LocalDateTime endTime, Sort sort);

}
