/**
 * 
 */
package com.avc.mis.beta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.avc.mis.beta.dao.DeletableDAO;
import com.avc.mis.beta.dto.item.BillOfMaterialsDTO;
import com.avc.mis.beta.entities.item.BillOfMaterials;
import com.avc.mis.beta.repositories.BillOfMaterialsRepository;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * 
 * @author Zvi
 *
 */
@Service
@Getter(value = AccessLevel.PRIVATE)
@Transactional(readOnly = true)
public class BillOfMaterialService {
	
	@Autowired private DeletableDAO dao;

	@Autowired private BillOfMaterialsRepository bomRepository;
	
	@Transactional(rollbackFor = Throwable.class, readOnly = false)
	public void addBillOfMaterials(BillOfMaterialsDTO billOfMaterials) {
		dao.addEntity(billOfMaterials.fillEntity(new BillOfMaterials()));
	}
	
	public BillOfMaterialsDTO getBillOfMaterialsByProduct(int productId) {
		BillOfMaterials billOfMaterials = getBomRepository().findBillOfMaterialsByProduct(productId);
		
		return new BillOfMaterialsDTO(billOfMaterials);	
	}
	
	@Transactional(rollbackFor = Throwable.class, readOnly = false)
	public void editBillOfMaterials(BillOfMaterialsDTO billOfMaterials) {
		dao.editEntity(billOfMaterials.fillEntity(new BillOfMaterials()));
	}
	
	@Transactional(rollbackFor = Throwable.class, readOnly = false)
	public void removeBillOfMaterials(Integer billOfMaterialsId) {
		dao.permenentlyRemoveEntity(BillOfMaterials.class, billOfMaterialsId);
	}
	
	
	
		

}