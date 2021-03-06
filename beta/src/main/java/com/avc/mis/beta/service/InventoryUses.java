/**
 * 
 */
package com.avc.mis.beta.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.avc.mis.beta.dao.ProcessDAO;
import com.avc.mis.beta.dto.basic.ItemWithUnitDTO;
import com.avc.mis.beta.dto.process.InventoryUseDTO;
import com.avc.mis.beta.dto.process.group.StorageMovesGroupDTO;
import com.avc.mis.beta.dto.process.storages.StorageBaseDTO;
import com.avc.mis.beta.dto.process.storages.StorageMoveDTO;
import com.avc.mis.beta.entities.enums.ItemGroup;
import com.avc.mis.beta.entities.enums.ProcessName;
import com.avc.mis.beta.entities.enums.ProductionFunctionality;
import com.avc.mis.beta.entities.process.InventoryUse;
import com.avc.mis.beta.repositories.InventoryUseRepository;
import com.avc.mis.beta.repositories.ValueTablesRepository;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Service for accessing and manipulating inventory use.
 * Inventory Use is used to remove inventory either because it's used without a process or to adjust inventory.
 * 
 * @author Zvi
 *
 */
@Service
@Getter(value = AccessLevel.PRIVATE)
@Transactional(readOnly = true)
public class InventoryUses {
	
	@Autowired private ProcessDAO dao;

	@Autowired private InventoryUseRepository inventoryUseRepository;
	@Autowired private ValueTablesRepository valueTablesRepository;
	@Autowired private ProcessReader processReader;

	
	@Transactional(rollbackFor = Throwable.class, readOnly = false, isolation = Isolation.SERIALIZABLE)
	public Integer addGeneralInventoryUse(InventoryUseDTO inventoryUse) {
		inventoryUse.setProcessName(ProcessName.GENERAL_USE);
		if(inventoryUse.getProductionLine() == null || 
				valueTablesRepository.findFunctionalityByProductionLine(inventoryUse.getProductionLine().getId()) != ProductionFunctionality.GENERAL_USE) {
			throw new IllegalStateException("Inventory Use has to have a Production Line with ProductionFunctionality.GENERAL_USE");
		}
		//Check that used items are from general
		if(!isUsedInItemGroup(inventoryUse.getStorageMovesGroups(), ItemGroup.GENERAL)) {
			throw new IllegalArgumentException("Inventory use can only be for GENERAL item groups");
		}
		return dao.addRelocationProcessEntity(inventoryUse, InventoryUse::new);
	}
	
	@Transactional(rollbackFor = Throwable.class, readOnly = false, isolation = Isolation.SERIALIZABLE)
	public Integer addProductInventoryUse(InventoryUseDTO inventoryUse) {
		inventoryUse.setProcessName(ProcessName.PRODUCT_USE);
		if(inventoryUse.getProductionLine() == null || 
				valueTablesRepository.findFunctionalityByProductionLine(inventoryUse.getProductionLine().getId()) != ProductionFunctionality.PRODUCT_USE) {
			throw new IllegalStateException("Inventory Use has to have a Production Line with ProductionFunctionality.PRODUCT_USE");
		}
		//Check that used items are from product (cashew)
		if(!isUsedInItemGroup(inventoryUse.getStorageMovesGroups(), ItemGroup.PRODUCT)) {
			throw new IllegalArgumentException("Inventory use can only be for PRODUCT item groups");
		}
		return dao.addRelocationProcessEntity(inventoryUse, InventoryUse::new);
		
	}
	
	public InventoryUseDTO getInventoryUse(int processId) {
		InventoryUseDTO inventoryUseDTO = new InventoryUseDTO();
		inventoryUseDTO.setGeneralProcessInfo(getInventoryUseRepository()
				.findGeneralProcessInfoByProcessId(processId, InventoryUse.class)
				.orElseThrow(
						()->new IllegalArgumentException("No inventory use with given process id")));
		inventoryUseDTO.setPoProcessInfo(getInventoryUseRepository()
				.findPoProcessInfoByProcessId(processId, InventoryUse.class).orElse(null));

		getProcessReader().setRelocationProcessCollections(inventoryUseDTO);
		
		return inventoryUseDTO;
	}

	@Transactional(rollbackFor = Throwable.class, readOnly = false, isolation = Isolation.SERIALIZABLE)
	public void editGeneralInventoryUse(InventoryUseDTO inventoryUse) {
		if(inventoryUse.getProductionLine() == null || 
				inventoryUseRepository.findFunctionalityByProductionLine(inventoryUse.getProductionLine().getId()) != ProductionFunctionality.GENERAL_USE) {
			throw new IllegalStateException("Inventory Use has to have a Production Line with ProductionFunctionality.GENERAL_USE");
		}
		//Check that used items are from general
		if(!isUsedInItemGroup(inventoryUse.getStorageMovesGroups(), ItemGroup.GENERAL)) {
			throw new IllegalArgumentException("Inventory use can only be for GENERAL item groups");
		}
		
		dao.editRelocationProcessEntity(inventoryUse, InventoryUse::new);
	}

	@Transactional(rollbackFor = Throwable.class, readOnly = false, isolation = Isolation.SERIALIZABLE)
	public void editProductInventoryUse(InventoryUseDTO inventoryUse) {
		if(inventoryUse.getProductionLine() == null || 
				inventoryUseRepository.findFunctionalityByProductionLine(inventoryUse.getProductionLine().getId()) != ProductionFunctionality.PRODUCT_USE) {
			throw new IllegalStateException("Inventory Use has to have a Production Line with ProductionFunctionality.PRODUCT_USE");
		}
		//Check that used items are from product (cashew)
		if(!isUsedInItemGroup(inventoryUse.getStorageMovesGroups(), ItemGroup.PRODUCT)) {
			throw new IllegalArgumentException("Inventory use can only be for PRODUCT item groups");
		}
		
		dao.editRelocationProcessEntity(inventoryUse, InventoryUse::new);
	}

	private boolean isUsedInItemGroup(List<StorageMovesGroupDTO> storageMovesGroups, ItemGroup itemGroup) {
		Set<Integer> usedStorageIds = null;
		for(StorageMovesGroupDTO smg: storageMovesGroups) {
			usedStorageIds = smg.getStorageMovesField().stream().map(StorageMoveDTO::getStorage).map(StorageBaseDTO::getId).collect(Collectors.toSet());
		}
		if(usedStorageIds != null) {
			List<ItemWithUnitDTO> items = getValueTablesRepository().findStoragesItems(usedStorageIds);
			if(items.stream().anyMatch(i -> i.getGroup() != itemGroup)) {
				return false;
			}
		}
		return true;
	}

}
