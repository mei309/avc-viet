/**
 * 
 */
package com.avc.mis.beta;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.avc.mis.beta.dto.basic.BasicValueEntity;
import com.avc.mis.beta.dto.basic.DataObjectWithName;
import com.avc.mis.beta.dto.basic.ItemWithMeasureUnit;
import com.avc.mis.beta.dto.basic.ItemWithUnitDTO;
import com.avc.mis.beta.dto.basic.PoCodeBasic;
import com.avc.mis.beta.dto.codes.ProductPoCodeDTO;
import com.avc.mis.beta.dto.data.DataObject;
import com.avc.mis.beta.dto.data.SupplierDTO;
import com.avc.mis.beta.dto.process.PoDTO;
import com.avc.mis.beta.dto.process.QualityCheckDTO;
import com.avc.mis.beta.dto.process.ReceiptDTO;
import com.avc.mis.beta.dto.process.SampleReceiptDTO;
import com.avc.mis.beta.dto.process.collectionItems.CashewItemQualityDTO;
import com.avc.mis.beta.dto.process.collectionItems.ItemWeightDTO;
import com.avc.mis.beta.dto.process.collectionItems.OrderItemDTO;
import com.avc.mis.beta.dto.process.collectionItems.ProcessFileDTO;
import com.avc.mis.beta.dto.process.group.ProcessItemDTO;
import com.avc.mis.beta.dto.process.group.ReceiptItemDTO;
import com.avc.mis.beta.dto.process.group.SampleItemDTO;
import com.avc.mis.beta.dto.process.storages.StorageDTO;
import com.avc.mis.beta.dto.process.storages.StorageWithSampleDTO;
import com.avc.mis.beta.dto.values.ItemDTO;
import com.avc.mis.beta.entities.data.Supplier;
import com.avc.mis.beta.entities.embeddable.AmountWithCurrency;
import com.avc.mis.beta.entities.embeddable.AmountWithUnit;
import com.avc.mis.beta.entities.embeddable.RawDamage;
import com.avc.mis.beta.entities.embeddable.RawDefects;
import com.avc.mis.beta.entities.enums.EditStatus;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.enums.ProcessStatus;
import com.avc.mis.beta.entities.process.QualityCheck;
import com.avc.mis.beta.entities.process.collectionItems.OrderItem;
import com.avc.mis.beta.entities.values.Item;
import com.avc.mis.beta.entities.values.Warehouse;
import com.avc.mis.beta.repositories.ValueTablesRepository;
import com.avc.mis.beta.service.ObjectWriter;
import com.avc.mis.beta.service.Orders;
import com.avc.mis.beta.service.ProcessInfoWriter;
import com.avc.mis.beta.service.QualityChecks;
import com.avc.mis.beta.service.Receipts;
import com.avc.mis.beta.service.Samples;
import com.avc.mis.beta.service.Suppliers;
import com.avc.mis.beta.service.ValueTablesReader;
import com.avc.mis.beta.service.ValueWriter;

/**
 * @author Zvi
 *
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@WithUserDetails("eli")
public class GeneralTest {
	
	static final Integer PO_CODE = 800253;
	static final Integer NUM_PO_ITEMS = 2;
	static final Integer NUM_OF_CHECKS = 1;
	
	@Autowired TestService service;
	@Autowired ValueTablesRepository valueTablesRepository;
	
	@Autowired ValueTablesReader valueTablesReader;
	@Autowired Suppliers suppliers;
	@Autowired Orders orders;
	@Autowired Receipts receipts;
	@Autowired QualityChecks checks;
	@Autowired Samples samples;
	
	@Autowired ObjectWriter objectWriter;
	@Autowired ValueWriter valueWriter;
	@Autowired ProcessInfoWriter processInfoWriter;
	
	@Test
	void orderAndReceiveTest() {
		//create basic supplier with all existing supply categories
		SupplierDTO supplier = new SupplierDTO();
		supplier.setName("Test supplier" + PO_CODE);
		supplier.setSupplyCategories(valueTablesReader.getAllSupplyCategories().stream().collect(Collectors.toSet()));
		Integer supplierId = suppliers.addSupplier(supplier);
		SupplierDTO fetchedSupplier = suppliers.getSupplier(supplierId);
		assertEquals(supplier, fetchedSupplier, "Supplier not added or fetched correctly");
		
		//create a cashew order with 2 order lines
		PoDTO po = new PoDTO();
		ProductPoCodeDTO poCode = new ProductPoCodeDTO();
		poCode.setCode(Integer.toString(PO_CODE));
//		poCode.setContractType(valueTablesRepository.findContractTypeByCodeAndCurrency("VAT", Currency.getInstance("VND")));
		poCode.setContractType(service.getContractType());
		poCode.setSupplier(new DataObjectWithName<Supplier>(fetchedSupplier.getId(), fetchedSupplier.getVersion(), fetchedSupplier.getName()));
		Integer poCodeId = objectWriter.addPoCode(poCode);
		poCode.setId(poCodeId);
		PoCodeBasic poCodeBasic = new PoCodeBasic();
		poCodeBasic.setId(poCodeId);
		po.setPoCode(poCodeBasic);
		po.setRecordedTime(LocalDateTime.now());
		List<OrderItemDTO> orderItems = new ArrayList<OrderItemDTO>();
		List<ItemDTO> items = valueTablesReader.getAllItems();
		for(int i=0; i < NUM_PO_ITEMS; i++) {
			OrderItemDTO orderItem = new OrderItemDTO();
			orderItems.add(orderItem);
			ItemDTO item = items.get(i);
			orderItem.setItem(new ItemWithMeasureUnit(item.getId(), item.getValue(), item.getMeasureUnit()));
			orderItem.setNumberUnits(new AmountWithUnit(BigDecimal.valueOf(35000), item.getMeasureUnit()));
//			orderItem.setCurrency("USD");
//			orderItem.setMeasureUnit("LBS");
			orderItem.setUnitPrice(new AmountWithCurrency("2.99", "USD"));
			orderItem.setDeliveryDate(LocalDate.now().toString());			
		}
		po.setOrderItems(orderItems);
		Integer poId = orders.addCashewOrder(po);
		PoDTO poDTO = orders.getOrderByProcessId(poId);
		assertEquals(po, poDTO, "PO not added or fetched correctly");
		
		//change order process life cycle to lock process for editing
		processInfoWriter.setEditStatus(EditStatus.LOCKED, poId);
		poDTO = orders.getOrderByProcessId(poId);
		assertEquals(EditStatus.LOCKED, poDTO.getEditStatus(), "Didn't change life cycle record edit status");
		try {
			processInfoWriter.setProcessStatus(ProcessStatus.PENDING, poId);
			fail("Should not be able to change to previous life cycle status");
		} catch (Exception e1) {}
		//check that process can't be edited after it's locked
		po.setDowntime(Duration.ofHours(24));
		try {
			orders.editOrder(po);
			fail("Should not be able to edit at locked status life cycle");
		} catch (Exception e1) {}
		
		//receive both order lines in parts and different storages
		ReceiptDTO receipt = new ReceiptDTO();
		receipt.setPoCode(new PoCodeBasic(poCode));
		receipt.setRecordedTime(LocalDateTime.now());
		List<ReceiptItemDTO> receiptItems = new ArrayList<>();
		List<BasicValueEntity<Warehouse>> warehouses = valueTablesReader.getAllWarehousesBasic();
		List<OrderItemDTO> fetchedOrderItems = poDTO.getOrderItems();
		for(int i=0; i < NUM_PO_ITEMS; i++) {
			ReceiptItemDTO receiptItem = new ReceiptItemDTO();
			ItemWithMeasureUnit item = fetchedOrderItems.get(i).getItem();
			receiptItem.setItem(new ItemWithUnitDTO((Item)item.fillEntity(new Item())));
			receiptItem.setMeasureUnit(item.getMeasureUnit());
			receiptItem.setReceivedOrderUnits(new AmountWithUnit(BigDecimal.valueOf(35000), item.getMeasureUnit()));
			receiptItem.setUnitPrice(new AmountWithCurrency("2.99", "USD"));
			receiptItem.setOrderItem(new DataObject<OrderItem>(fetchedOrderItems.get(i).getId(), fetchedOrderItems.get(i).getVersion()));
			
			StorageWithSampleDTO[] storageForms = new StorageWithSampleDTO[2];
			StorageWithSampleDTO storage = new StorageWithSampleDTO();
			storage.setUnitAmount(BigDecimal.valueOf(50));
			storage.setNumberUnits(BigDecimal.valueOf(326));
			storage.setWarehouseLocation(warehouses.get(i));
//			storage.setMeasureUnit("KG");
			storageForms[0] = storage;
			
			storage = new StorageWithSampleDTO();
			storage.setUnitAmount(BigDecimal.valueOf(26));
			storage.setNumberUnits(BigDecimal.valueOf(1));
			storage.setWarehouseLocation(warehouses.get(i));
//			storage.setMeasureUnit("KG");
			storageForms[1] = storage;
			
			receiptItem.setStorageForms(storageForms);
			receiptItems.add(receiptItem);
		}
		receipt.setReceiptItems(receiptItems);
		try {
			receipt.setId(receipts.addCashewOrderReceipt(receipt));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw e1;
		}
		ReceiptDTO receiptDTO;
		receiptDTO = receipts.getReceiptByProcessId(receipt.getId());
		assertEquals(receipt, receiptDTO, "Order Receipt not added or fetched correctly");
		
		
		//add QC for received order
		QualityCheckDTO check = new QualityCheckDTO();
		check.setPoCode(new PoCodeBasic(poCode));
		check.setRecordedTime(LocalDateTime.now());
		check.setCheckedBy("avc lab");
		List<CashewItemQualityDTO> rawItemQualities = new ArrayList<CashewItemQualityDTO>();
		List<ProcessItemDTO> processItems = new ArrayList<ProcessItemDTO>();
		for(int i=0; i < NUM_PO_ITEMS; i++) {
			CashewItemQualityDTO rawItemQuality = new CashewItemQualityDTO();
			rawItemQualities.add(rawItemQuality);
			ItemWithMeasureUnit item = orderItems.get(i).getItem();
			rawItemQuality.setItem(new BasicValueEntity<Item>(item.getId(), item.getValue()));
			rawItemQuality.setMeasureUnit(MeasureUnit.OZ);
			rawItemQuality.setSampleWeight(BigDecimal.valueOf(8).setScale(QualityCheck.SCALE));
			rawItemQuality.setNumberOfSamples(BigInteger.TEN);
			rawItemQuality.setDefects(new RawDefects());
			rawItemQuality.setDamage(new RawDamage());
			
			ProcessItemDTO processItem = new ProcessItemDTO();
			processItems.add(processItem);
			ItemWithUnitDTO itemWithUnitDTO = new ItemWithUnitDTO(orderItems.get(i).getItem().fillEntity(new Item()));
			processItem.setItem(itemWithUnitDTO);
			processItem.setMeasureUnit(item.getMeasureUnit());
			
			List<StorageDTO> QCStorageForms = new ArrayList<StorageDTO>();
			StorageDTO QCStorageForm = new StorageDTO();
			QCStorageForms.add(QCStorageForm);
			QCStorageForm.setUnitAmount(BigDecimal.valueOf(8));
			QCStorageForm.setNumberUnits(BigDecimal.valueOf(2));
			QCStorageForm.setWarehouseLocation(warehouses.get(i));
//			QCStorageForm.setMeasureUnit("OZ");
			
			processItem.setStorageForms(QCStorageForms);
			
		}
		check.setProcessItems(processItems);
		check.setTestedItems(rawItemQualities);
		Integer checkId;
		try {
			checkId = checks.addCashewReceiptCheck(check);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			throw e2;
		}
		QualityCheckDTO checkDTO;
		checkDTO = checks.getQcByProcessId(checkId);
		assertEquals(check, checkDTO, "QC not added or fetched correctly");

		ProcessFileDTO processFile = new ProcessFileDTO(null, null, checkId, "address", 
				"description", "remarks", null, null);
		objectWriter.addProcessFile(processFile);
		try {
			
			check.setProcessFiles(Arrays.asList(processFile));
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			throw e2;
		}
		checkDTO = checks.getQcByProcessId(checkId);
//		System.out.println(checkDTO);
//		fail("finished");
		try {
			assertEquals(check, checkDTO, "QC not added or fetched correctly");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw e1;
		}

		//add receipt sample check for received orders
		SampleReceiptDTO sampleReceipt = new SampleReceiptDTO();
		sampleReceipt.setPoCode(new PoCodeBasic(poCode));
		sampleReceipt.setRecordedTime(LocalDateTime.now());
		List<SampleItemDTO> sampleItems = new ArrayList<SampleItemDTO>();
		sampleReceipt.setSampleItems(sampleItems);
		SampleItemDTO sampleItem = new SampleItemDTO();
		sampleItems.add(sampleItem);
		ItemDTO item = items.get(0);
		sampleItem.setItem(new BasicValueEntity(item.getId(), item.getValue()));
		sampleItem.setMeasureUnit(MeasureUnit.KG);
		List<ItemWeightDTO> itemWeights = new ArrayList<ItemWeightDTO>();
		sampleItem.setItemWeights(itemWeights);
		ItemWeightDTO itemWeight = new ItemWeightDTO();
		itemWeights.add(itemWeight);
		itemWeight.setUnitAmount(BigDecimal.valueOf(50));
		itemWeight.setNumberUnits(BigDecimal.TEN);
		itemWeight.setNumberOfSamples(BigInteger.valueOf(30));
		itemWeight.setAvgTestedWeight(BigDecimal.valueOf(50.01));
		sampleItem.setItemWeights(itemWeights);
		sampleItem.setSampleContainerWeight(BigDecimal.valueOf(0.002));
		sampleItem = new SampleItemDTO();
		sampleItem.setItem(new BasicValueEntity(item.getId(), item.getValue()));
		sampleItem.setMeasureUnit(MeasureUnit.KG);
//		itemWeights = new ArrayList<ItemWeightDTO>();
		itemWeight = new ItemWeightDTO();
		itemWeights.add(itemWeight);
		itemWeight.setUnitAmount(BigDecimal.valueOf(26));
		itemWeight.setNumberUnits(BigDecimal.TEN);
		itemWeight.setNumberOfSamples(BigInteger.valueOf(1));
		itemWeight.setAvgTestedWeight(BigDecimal.valueOf(26.01));
		sampleItem.setItemWeights(itemWeights);
		sampleItem.setSampleContainerWeight(BigDecimal.valueOf(0.002));
		Integer sampleId;
		try {
			sampleId = samples.addSampleReceipt(sampleReceipt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		SampleReceiptDTO sampleReceiptDTO;
		System.out.println("line 202");
		sampleReceiptDTO = samples.getSampleReceiptByProcessId(sampleId);
		assertEquals(sampleReceipt, sampleReceiptDTO, "Receipt sample not added or fetched correctly");
		
		
		
		//print all
		System.out.println("Supplier: " + fetchedSupplier);
		System.out.println("Purchase Order: " + poDTO);
		System.out.println("Order receipt: " + receiptDTO);
		System.out.println("QC test: " + checkDTO);
		System.out.println("Receipt sample: " + sampleReceiptDTO);
		
		
		//remove all
//		samples.removeSampleReceipt(sampleReceipt.getId());
//		checks.removeCheck(check.getId());
//		receipts.removeReceipt(receiptDTO.getId());
//		orders.removeOrder(poDTO.getId());
//		suppliers.permenentlyRemoveEntity(poCode);
//		suppliers.permenentlyRemoveSupplier(supplierDTO.getId());
		
		processInfoWriter.removeAllProcesses(poCode.getId());
		suppliers.permenentlyRemoveSupplier(fetchedSupplier.getId());

	}
}
