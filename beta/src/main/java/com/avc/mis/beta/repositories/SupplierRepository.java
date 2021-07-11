/**
 * 
 */
package com.avc.mis.beta.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.Query;

import com.avc.mis.beta.dto.data.DataObjectWithName;
import com.avc.mis.beta.dto.generic.ValueObject;
import com.avc.mis.beta.dto.view.SupplierRow;
import com.avc.mis.beta.entities.data.CompanyContact;
import com.avc.mis.beta.entities.data.Supplier;
import com.avc.mis.beta.entities.enums.SupplyGroup;
import com.avc.mis.beta.service.report.row.SupplierQualityRow;

/**
 * Spring repository for accessing information of company suppliers.
 * 
 * @author Zvi
 *
 */
public interface SupplierRepository extends BaseRepository<Supplier> {
	
	@Query("select new com.avc.mis.beta.dto.data.DataObjectWithName(s.id, s.version, s.name) "
			+ "from Supplier s "
			+ "left join s.supplyCategories c "
			+ "where c.id = :categoryId "
				+ "and s.active = true")
	List<DataObjectWithName<Supplier>> findSuppliersByCategoryBasic(Integer categoryId);
	
	@Query("select distinct new com.avc.mis.beta.dto.data.DataObjectWithName(s.id, s.version, s.name) "
			+ "from Supplier s "
			+ "left join s.supplyCategories c "
			+ "where c.supplyGroup = :supplyGroup "
				+ "and s.active = true "
			+ "ORDER BY s.name")
	List<DataObjectWithName<Supplier>> findSuppliersByGroupBasic(SupplyGroup supplyGroup);
		
	@Query("select new com.avc.mis.beta.dto.data.DataObjectWithName(s.id, s.version, s.name) "
			+ "from Supplier s "
			+ "where s.active = true")
	List<DataObjectWithName<Supplier>> findAllSuppliersBasic();
	
	@Query("select s from Supplier s "
			+ "left join fetch s.contactDetails cd "
			+ "where s.id = :id")
	Optional<Supplier> findById(Integer id);
	
	@Query("select cc from CompanyContact cc "
			+ "left join fetch cc.position "
			+ "left join fetch cc.person p "
				+ "left join fetch p.idCard id "
				+ "left join fetch p.contactDetails cd "
			+ "where cc.company.id = :id "
				+ "and cc.active = true")
	List<CompanyContact> findCompanyContactsByCompnyId(Integer id);
	
	@Query("select new com.avc.mis.beta.dto.view.SupplierRow(s.id, s.name, cd.id) "
			+ "from Supplier s "
			+ "left join s.contactDetails cd "
			+ "where s.active = true "
			+ "order by s.name ")
	List<SupplierRow> findAllSupplierRows();
	
	@Query("select new com.avc.mis.beta.dto.generic.ValueObject(cd.id, p.value) "
			+ "from Phone p "
				+ "join p.contactDetails cd ")
	Stream<ValueObject<String>> findAllPhoneValues();
	
	@Query("select new com.avc.mis.beta.dto.generic.ValueObject(cd.id, e.value) "
			+ "from Email e "
				+ "join e.contactDetails cd ")
	Stream<ValueObject<String>> findAllEmailValues();

	@Query("select new com.avc.mis.beta.dto.generic.ValueObject(s.id, c.value) "
			+ "from Supplier s "
				+ "join s.supplyCategories c ")
	Stream<ValueObject<String>> findAllSupplyCategoryValues();

//	@Query("select new com.avc.mis.beta.service.report.row.SupplierQualityRow( "
//			+ "r.id, po_code.id, po_code.code, ct.code, ct.suffix, s.name,  "
//			+ "item.id, item.value, item.measureUnit, item.itemGroup, item_unit, type(item), "
//			+ "units.amount, units.measureUnit, "
//			+ "ro_units.amount, ro_units.measureUnit, "
//			+ "r.recordedTime, lc.processStatus, "
//			+ "SUM(sf.unitAmount * sf.numberUnits * uom.multiplicand / uom.divisor), item.measureUnit, "
//			+ "function('GROUP_CONCAT', function('DISTINCT', sto.value)), "
//			+ "extra.amount, extra.measureUnit) "
//		+ "from Receipt r "
//			+ "join r.lifeCycle lc "
//			+ "join r.poCode po_code "
//				+ "join po_code.supplier s "
//				+ "join po_code.contractType ct "
//			+ "join r.processItems pi "
//				+ "left join pi.extraRequested extra "
//				+ "join pi.item item "
//					+ "join item.unit item_unit "
//				+ "join pi.storageForms sf "
//			+ "join UOM uom "
//				+ "on uom.fromUnit = pi.measureUnit and uom.toUnit = item.measureUnit "
//			+ "join r.processType t "
//		+ "where (s.id = :supplierId or :supplierId is null)"
//			+ "and (:startTime is null or r.recordedTime >= :startTime) "
//			+ "and (:endTime is null or r.recordedTime < :endTime) "
//		+ "group by po_code "
//		+ "order by s, r.recordedTime ")
//	List<SupplierQualityRow> findSupplierWithPos(Integer supplierId, LocalDateTime startTime, LocalDateTime endTime);
}
