/**
 * 
 */
package com.avc.mis.beta;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.avc.mis.beta.dto.process.OrderItemDTO;
import com.avc.mis.beta.dto.process.PoDTO;
import com.avc.mis.beta.dto.process.ReceiptDTO;
import com.avc.mis.beta.dto.values.ReceiptRow;
import com.avc.mis.beta.entities.data.Supplier;
import com.avc.mis.beta.entities.process.ExtraAdded;
import com.avc.mis.beta.entities.process.OrderItem;
import com.avc.mis.beta.entities.process.PoCode;
import com.avc.mis.beta.entities.process.Receipt;
import com.avc.mis.beta.entities.process.ReceiptItem;
import com.avc.mis.beta.entities.process.Storage;
import com.avc.mis.beta.entities.values.ContractType;
import com.avc.mis.beta.entities.values.Item;
import com.avc.mis.beta.entities.values.Warehouse;
import com.avc.mis.beta.service.OrderReceipts;
import com.avc.mis.beta.service.Orders;
import com.avc.mis.beta.service.Suppliers;

/**
 * @author Zvi
 *
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WithUserDetails("eli")
public class ReceiptTest {
	
	public static int RECEIPT_PROCESS_NO = 800024;
	
	@Autowired OrderReceipts receipts;
	
	@Autowired Orders orders;
	
	@Autowired Suppliers suppliers;

	private Receipt basicReceipt() {
		//build order receipt
		Receipt receipt = new Receipt();
		PoCode poCode = new PoCode();
		poCode.setCode(RECEIPT_PROCESS_NO);
		Supplier supplier = SuppliersTest.basicSupplier();
		suppliers.addSupplier(supplier);
		poCode.setSupplier(supplier);
		ContractType contractType = new ContractType();
		contractType.setId(1);
		poCode.setContractType(contractType);
		receipt.setPoCode(poCode);
		//build process
		receipt.setRecordedTime(OffsetDateTime.now());
		//add order items
		ReceiptItem[] items = processItems(OrdersTest.NUM_ITEMS);	
		for(ReceiptItem item: items)
			System.out.println(item);
		receipt.setReceiptItems(items);
		return receipt;
	}
	
	private Receipt orderReceipt() {
		//build order receipt
		Receipt receipt = new Receipt();
		PoCode poCode = new PoCode();
		poCode.setCode(OrdersTest.PROCESS_NO);
		receipt.setPoCode(poCode);
		//build process
		receipt.setRecordedTime(OffsetDateTime.now());
		//add order items
		PoDTO poDTO = orders.getOrder(OrdersTest.PROCESS_NO);
//		Supplier supplier = new Supplier();
//		supplier.setId(poDTO.getSupplier().getId());
//		supplier.setVersion(poDTO.getSupplier().getVersion());
//		receipt.setSupplier(supplier);
		ReceiptItem[] items = receiptItems(poDTO);				
		receipt.setReceiptItems(items);
		return receipt;
		
		
	}
	
	/**
	 * @param numItems
	 * @return
	 */
	private ReceiptItem[] receiptItems(PoDTO poDTO) {
		Set<OrderItemDTO> orderItems = poDTO.getOrderItems();
		ReceiptItem[] items = new ReceiptItem[orderItems.size()];
		Storage[] storageForms = new Storage[items.length];
		Warehouse storage = new Warehouse();
		OrderItem oi;
		storage.setId(1);
		int i=0;
		for(OrderItemDTO oItem: orderItems) {
			items[i] = new ReceiptItem();
			storageForms[i] = new Storage();
			Item item = new Item();
			item.setId(oItem.getItem().getId());
			items[i].setItem(item);
			storageForms[i].setUnitAmount(BigDecimal.valueOf(1000, 2));//because database is set to scale 2
			storageForms[i].setMeasureUnit("KG");
			storageForms[i].setNumberUnits(oItem.getNumberUnits().divide(BigDecimal.valueOf(10, 2)).setScale(2));
			storageForms[i].setWarehouseLocation(storage);
			items[i].setStorageForms(new Storage[] {storageForms[i]});
			oi  = new OrderItem();
			oi.setId(oItem.getId());
			oi.setVersion(oItem.getVersion());
			items[i].setOrderItem(oi);
			items[i].setExtraRequested(BigDecimal.valueOf(200));
			i++;
		}
		return items;
	}

	private ReceiptItem[] processItems(int numOfItems) {
		ReceiptItem[] items = new ReceiptItem[numOfItems];
		Storage[] storageForms = new Storage[items.length];
		Item item = new Item();
		item.setId(1);
		Warehouse storage = new Warehouse();
		storage.setId(1);
		OrderItem orderItem = new OrderItem();
		orderItem.setId(96);
		for(int i=0; i<items.length; i++) {
			items[i] = new ReceiptItem();
			storageForms[i] = new Storage();
			items[i].setItem(item);
			storageForms[i].setUnitAmount(BigDecimal.valueOf(1000, 2));//because database is set to scale 2
			storageForms[i].setMeasureUnit("KG");
			storageForms[i].setNumberUnits(new BigDecimal(i+1).setScale(2));
			storageForms[i].setWarehouseLocation(storage);
			items[i].setStorageForms(new Storage[] {storageForms[i]});
		}
//		Arrays.stream(items).forEach(i -> System.out.println(i));
		return items;
	}
	
//	@Disabled
	@Test
	void receiptTest() {
		//insert order receipt without order
		Receipt receipt = basicReceipt();
		try {
			receipts.addCashewReceipt(receipt);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw e1;
		}
		ReceiptDTO expected = new ReceiptDTO(receipt);
		ReceiptDTO actual = receipts.getReceiptByProcessId(receipt.getId());
		assertEquals(expected, actual, "failed test adding receipt without order");
		System.out.println(actual);

		//insert order receipt for order
		receipt = orderReceipt();
		receipts.addCashewOrderReceipt(receipt);
		try {
			expected = new ReceiptDTO(receipt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		actual = receipts.getReceiptByProcessId(receipt.getId());
		assertEquals(expected, actual, "failed test adding order receipt");
		System.out.println(actual);
		
		//add extra bonus
		ExtraAdded[] added = new ExtraAdded[1];
		added[0] = new ExtraAdded();
		added[0].setUnitAmount(BigDecimal.valueOf(500));//because database is set to scale 2
		added[0].setMeasureUnit("KG");
		added[0].setNumberUnits(new BigDecimal(4).setScale(2));
		receipts.addExtra(added, receipt.getProcessItems()[0].getId());
		receipt.getProcessItems()[0]
				.setStorageForms(ArrayUtils.addAll(receipt.getProcessItems()[0].getStorageForms(), added));
		try {
			expected = new ReceiptDTO(receipt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		try {
			actual = receipts.getReceiptByProcessId(receipt.getId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		assertEquals(expected, actual, "failed test adding extra bonus");
		
		//
		List<ReceiptRow> receiptRows = receipts.findCashewReceipts();
		receiptRows.forEach(r -> System.out.println(r));
	}



}
