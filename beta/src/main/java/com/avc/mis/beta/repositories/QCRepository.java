/**
 * 
 */
package com.avc.mis.beta.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.Query;

import com.avc.mis.beta.dto.basic.ValueEntityObject;
import com.avc.mis.beta.dto.process.collectionItems.CashewItemQualityDTO;
import com.avc.mis.beta.dto.process.info.QualityCheckInfo;
import com.avc.mis.beta.dto.values.CashewStandardDTO;
import com.avc.mis.beta.dto.view.CashewQcRow;
import com.avc.mis.beta.entities.enums.ProcessName;
import com.avc.mis.beta.entities.process.QualityCheck;
import com.avc.mis.beta.entities.values.Item;
/**
 * @author Zvi
 *
 */
public interface QCRepository extends PoProcessRepository<QualityCheck> {
	
	@Query("select new com.avc.mis.beta.dto.process.info.QualityCheckInfo(r.checkedBy, r.inspector, r.sampleTaker) "
		+ "from QualityCheck r "
		+ "where r.id = :processId ")
	QualityCheckInfo findQualityCheckInfo(int processId);

	@Query("select new com.avc.mis.beta.dto.process.collectionItems.CashewItemQualityDTO("
			+ "i.id, i.version, i.ordinal, item.id, item.value, "
			+ "i.measureUnit, i.sampleWeight, i.numberOfSamples, i.precentage, "
			+ "i.wholeCountPerLb, i.smallSize, i.ws, i.lp, i.breakage, "
			+ "i.foreignMaterial, i.humidity, "
			+ "def.scorched, def.deepCut, def.offColour, def.shrivel, def.desert, def.deepSpot, "
			+ "dam.mold, dam.dirty, dam.lightDirty, dam.decay, dam.insectDamage, dam.testa, " 
			+ "i.roastingWeightLoss, " 
			+ "i.colour, i.flavour) "
		+ "from CashewItemQuality i "
			+ "join i.item item "
			+ "join i.process p "
			+ "join i.defects def "
			+ "join i.damage dam "
		+ "where p.id = :processId "
		+ "order by i.ordinal ")
	List<CashewItemQualityDTO> findCheckItemsById(int processId);

	@Query("select new com.avc.mis.beta.dto.values.CashewStandardDTO("
			+ "i.id, i.value, i.standardOrganization, "
			+ "i.totalDefects, i.totalDamage, i.totalDefectsAndDamage, "
			+ "i.wholeCountPerLb, i.smallSize, i.ws, i.lp, i.breakage, "
			+ "i.foreignMaterial, i.humidity, " 
			+ "def.scorched, def.deepCut, def.offColour, def.shrivel, def.desert, def.deepSpot, "
			+ "dam.mold, dam.dirty, dam.lightDirty, dam.decay, dam.insectDamage, dam.testa, " 
			+ "i.roastingWeightLoss) "
		+ "from CashewStandard i "
			+ "join i.items item "
			+ "join i.defects def "
			+ "join i.damage dam "
		+ "where item.id = :itemId and i.standardOrganization = :standardOrganization "
			+ "and i.active = true")
	CashewStandardDTO findCashewStandard(Integer itemId, String standardOrganization);
	
	@Query("select new com.avc.mis.beta.dto.values.CashewStandardDTO("
			+ "i.id, i.value, i.standardOrganization, "
			+ "i.totalDefects, i.totalDamage, i.totalDefectsAndDamage, "
			+ "i.wholeCountPerLb, i.smallSize, i.ws, i.lp, i.breakage, "
			+ "i.foreignMaterial, i.humidity, " 
			+ "def.scorched, def.deepCut, def.offColour, def.shrivel, def.desert, def.deepSpot, "
			+ "dam.mold, dam.dirty, dam.lightDirty, dam.decay, dam.insectDamage, dam.testa, " 
			+ "i.roastingWeightLoss) "
		+ "from CashewStandard i "
//			+ "join i.items item "
			+ "join i.defects def "
			+ "join i.damage dam "
		+ "where i.active = true")
	List<CashewStandardDTO> findAllCashewStandardDTO();
	
	@Query("select new com.avc.mis.beta.dto.basic.ValueEntityObject(i.id, item.id, item.value) "
		+ "from CashewStandard i "
			+ "join i.items item "
		+ "where i.active = true")
	Stream<ValueEntityObject<Item>> findAllStandardItems();

	@Query("select new com.avc.mis.beta.dto.view.CashewQcRow( "
			+ "qc.id, po_code.id, po_code.code, ct.code, ct.suffix, s.name, "
			+ "qc.checkedBy, i.id, i.value, qc.recordedTime, lc.processStatus, "
			+ "ti.numberOfSamples, ti.sampleWeight, ti.precentage, "
			+ "ti.humidity, ti.breakage,"
				+ "def.scorched, def.deepCut, def.offColour, "
				+ "def.shrivel, def.desert, def.deepSpot, "
				+ "dam.mold, dam.dirty, dam.lightDirty, "
				+ "dam.decay, dam.insectDamage, dam.testa) "
		+ "from QualityCheck qc "
			+ "join qc.testedItems ti "
				+ "join ti.item i "
				+ "join ti.defects def "
				+ "join ti.damage dam "
			+ "join qc.poCode po_code "
				+ "join po_code.supplier s "
				+ "join po_code.contractType ct "
			+ "join qc.processType pt "
			+ "join qc.lifeCycle lc "
		+ "where pt.processName in :processNames "
			+ "and (po_code.id = :poId or :poId is null) "
			+ "and (:startTime is null or qc.recordedTime >= :startTime) "
			+ "and (:endTime is null or qc.recordedTime < :endTime) "
		+ "order by qc.recordedTime desc ")
	List<CashewQcRow> findCashewQualityChecks(ProcessName[] processNames, Integer poId, LocalDateTime startTime, LocalDateTime endTime);

}
