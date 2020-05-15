/**
 * 
 */
package com.avc.mis.beta.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.avc.mis.beta.dto.values.ItemBasic;
import com.avc.mis.beta.entities.ValueEntity;
import com.avc.mis.beta.entities.enums.SupplyGroup;
import com.avc.mis.beta.entities.values.Storage;

/**
 * Spring repository for accessing lists of {@link ValueEntity} entities, 
 * that usually serve as constants referenced by user recorded data.
 * 
 * @author Zvi
 *
 */
public interface ValueTablesRepository extends BaseRepository<ValueEntity> {

	@Query("select new com.avc.mis.beta.dto.values.ItemBasic(i.id, i.value) "
			+ "from Item i "
			+ "where i.supplyGroup = :supplyGroup "
				+ "and i.active = true")
	List<ItemBasic> findItemsByGroupBasic(SupplyGroup supplyGroup);

	
}