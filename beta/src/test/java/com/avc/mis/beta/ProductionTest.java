/**
 * 
 */
package com.avc.mis.beta;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.avc.mis.beta.dto.basic.PoCodeBasic;
import com.avc.mis.beta.dto.process.ProductionProcessDTO;
import com.avc.mis.beta.dto.process.ReceiptDTO;
import com.avc.mis.beta.dto.view.ProcessItemInventory;
import com.avc.mis.beta.entities.enums.DecisionType;
import com.avc.mis.beta.entities.enums.ProcessName;
import com.avc.mis.beta.entities.enums.ProcessStatus;
import com.avc.mis.beta.service.ProcessInfoWriter;
import com.avc.mis.beta.service.WarehouseManagement;
import com.avc.mis.beta.service.interfaces.ProductionProcessService;

/**
 * @author Zvi
 *
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@WithUserDetails("eli")
public class ProductionTest {
	
	@Autowired TestService service;
	@Autowired ProcessInfoWriter infoWriter;
	@Autowired ProductionProcessService productionService;
	@Autowired WarehouseManagement warehouseManagement;
	
//	@Disabled
	@Test
	void cleaningTest() {
		ReceiptDTO receipt = service.addBasicCashewReceipt();
		infoWriter.setUserProcessDecision(receipt.getId(), DecisionType.APPROVED, null, null);
		infoWriter.setProcessStatus(ProcessStatus.FINAL, receipt.getId());
		
		ProductionProcessDTO process = new ProductionProcessDTO();
		PoCodeBasic poCode = new PoCodeBasic();
		poCode.setId(receipt.getPoCode().getId());
		process.setPoCode(poCode);
		process.setRecordedTime(LocalDateTime.now());
		List<ProcessItemInventory> poInventory = warehouseManagement.getAvailableInventory(null, null, null, null, new Integer[] {receipt.getPoCode().getId()}, null);
		process.setUsedItemGroups(TestService.getUsedItemsGroupsDTOs(poInventory));
		process.setProcessItems(service.getProcessItemsDTOs(poInventory));
//		process.setWeightedPos(service.getProductWeightedPos(2));
		
		Integer processId;
		try {
			processId = productionService.addProductionProcess(process, ProcessName.CASHEW_CLEANING);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		
		ProductionProcessDTO expected = process;		
		ProductionProcessDTO actual = productionService.getProductionProcess(processId);		

		assertEquals(expected, actual, "Cleaning production process not added or fetched correctly");
		
		try {
			productionService.getProductionProcessesByType(ProcessName.CASHEW_CLEANING);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			throw e2;
		}
		System.out.println(actual);

		infoWriter.removeAllProcesses(receipt.getPoCode().getId());
		
	}
	
	@Disabled
	@Test
	void iterateCleaningTest() {
		int iterations = 2048;
		long totalTime = 0;
		long queryTime;
		ReceiptDTO receipt; List<ProcessItemInventory> poInventory; long start, end;
		for(int i=1; i<= iterations; i++) {
			System.out.println("iteration: " + i);
//			-------------------------------------
			receipt = service.addBasicCashewReceipt();
			infoWriter.setUserProcessDecision(receipt.getId(), DecisionType.APPROVED, null, null);
			infoWriter.setProcessStatus(ProcessStatus.FINAL, receipt.getId());
			
			ProductionProcessDTO process = new ProductionProcessDTO();
			PoCodeBasic poCode = new PoCodeBasic();
			poCode.setId(receipt.getPoCode().getId());
			process.setPoCode(poCode);
			process.setRecordedTime(LocalDateTime.now());
			poInventory = warehouseManagement.getAvailableInventory(null, null, null, null, new Integer[] {receipt.getPoCode().getId()}, null);
			process.setUsedItemGroups(TestService.getUsedItemsGroupsDTOs(poInventory));
			process.setProcessItems(service.getProcessItemsDTOs(poInventory));
			
			Integer processId;
			try {
				processId = productionService.addProductionProcess(process, ProcessName.CASHEW_CLEANING);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw e;
			}
			
			try {
				start = System.currentTimeMillis();
				productionService.getProductionProcessesByType(ProcessName.CASHEW_CLEANING);
				end  = System.currentTimeMillis();
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				throw e2;
			}
//			---------------------------------------------
//			queryTime = cleaning();
			totalTime += (end-start);
			System.out.println("Query Time in miliseconds: "+ (end-start) + " avarage: " + totalTime/i);
			
		}
		System.out.println("Avarage Time in miliseconds: "+ totalTime/iterations);
	}
	
//	@RepeatedTest(2048)
	void cleaning() {
		ReceiptDTO receipt = service.addBasicCashewReceipt();
		infoWriter.setUserProcessDecision(receipt.getId(), DecisionType.APPROVED, null, null);
		infoWriter.setProcessStatus(ProcessStatus.FINAL, receipt.getId());
		
		ProductionProcessDTO process = new ProductionProcessDTO();
		PoCodeBasic poCode = new PoCodeBasic();
		poCode.setId(receipt.getPoCode().getId());
		process.setPoCode(poCode);
		process.setRecordedTime(LocalDateTime.now());
		List<ProcessItemInventory> poInventory = warehouseManagement.getAvailableInventory(null, null, null, null, new Integer[] {receipt.getPoCode().getId()}, null);
		process.setUsedItemGroups(TestService.getUsedItemsGroupsDTOs(poInventory));
		process.setProcessItems(service.getProcessItemsDTOs(poInventory));
		
		Integer processId;
		try {
			processId = productionService.addProductionProcess(process, ProcessName.CASHEW_CLEANING);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		
		try {
			long start = System.currentTimeMillis();
			productionService.getProductionProcessesByType(ProcessName.CASHEW_CLEANING);
			long end  = System.currentTimeMillis();
			System.out.println("Query Time in miliseconds: "+ (end-start));
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			throw e2;
		}
	}
}
