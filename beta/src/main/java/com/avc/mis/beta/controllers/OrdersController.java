/**
 * 
 */
package com.avc.mis.beta.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.avc.mis.beta.dto.basic.DataObjectWithName;
import com.avc.mis.beta.dto.basic.PoCodeBasic;
import com.avc.mis.beta.dto.basic.PoCodeBasicWithProductCompany;
import com.avc.mis.beta.dto.codes.GeneralPoCodeDTO;
import com.avc.mis.beta.dto.codes.PoCodeDTO;
import com.avc.mis.beta.dto.codes.ProductPoCodeDTO;
import com.avc.mis.beta.dto.process.PoDTO;
import com.avc.mis.beta.dto.process.ReceiptDTO;
import com.avc.mis.beta.dto.process.SampleReceiptDTO;
import com.avc.mis.beta.dto.process.storages.ExtraAddedDTO;
import com.avc.mis.beta.dto.values.ContractTypeDTO;
import com.avc.mis.beta.dto.view.PoItemRow;
import com.avc.mis.beta.dto.view.ReceiptRow;
import com.avc.mis.beta.entities.codes.MixPoCode;
import com.avc.mis.beta.entities.data.Supplier;
import com.avc.mis.beta.entities.enums.SupplyGroup;
import com.avc.mis.beta.service.ObjectTablesReader;
import com.avc.mis.beta.service.ObjectWriter;
import com.avc.mis.beta.service.Orders;
import com.avc.mis.beta.service.ProcessInfoWriter;
import com.avc.mis.beta.service.Receipts;
import com.avc.mis.beta.service.Samples;
import com.avc.mis.beta.service.ValueTablesReader;
import com.avc.mis.beta.service.report.OrderReports;
import com.avc.mis.beta.service.report.ReceiptReports;


/**
 * @author Zvi
 *
 */
@RestController
@RequestMapping(path = "/api/orders")
public class OrdersController {
	
	@Autowired
	private Orders ordersDao;
	
	@Autowired
	private ObjectWriter objectWriter;
	
	@Autowired
	private ObjectTablesReader objectTableReader;
	
	@Autowired
	private Samples samples;
	
	@Autowired
	private Receipts orderRecipt;
	
	@Autowired
	private ValueTablesReader refeDao;
	
	@Autowired
	private OrderReports orderReports;

	@Autowired
	private ProcessInfoWriter processInfoWriter;
	
	@Autowired
	private ReceiptReports receiptReports;
	
	@PostMapping(value="/addCashewOrder")
	public PoDTO addCashewOrder(@RequestBody PoDTO po) {
		Integer poId = ordersDao.addCashewOrder(po);
		return ordersDao.getOrderByProcessId(poId);
	}
	
	@PostMapping(value="/addGeneralOrder")
	public PoDTO addGeneralOrder(@RequestBody PoDTO po) {
		Integer poId = ordersDao.addGeneralOrder(po);
		return ordersDao.getOrderByProcessId(poId);
	}
	
	@PostMapping(value="/receiveCashewOrder")
	public ReceiptDTO receiptCashewOrder(@RequestBody ReceiptDTO receipt) {
		Integer id = orderRecipt.addCashewOrderReceipt(receipt);
		return orderRecipt.getReceiptByProcessId(id);
	}
	
	@PostMapping(value="/receiveCashewNoOrder")
	public ReceiptDTO addCashewReceipt(@RequestBody ReceiptDTO receipt) {
		Integer id = orderRecipt.addCashewReceipt(receipt);
		return orderRecipt.getReceiptByProcessId(id);
	}
	
	@PostMapping(value="/receiveGeneralOrder")
	public ReceiptDTO receiveGeneralOrder(@RequestBody ReceiptDTO receipt) {
		Integer id = orderRecipt.addGeneralOrderReceipt(receipt);
		return orderRecipt.getReceiptByProcessId(id);
	}
	
	@PostMapping(value="/receiveSample")
	public SampleReceiptDTO receiveSample(@RequestBody SampleReceiptDTO sample) {
		Integer id = samples.addSampleReceipt(sample);
		return samples.getSampleReceiptByProcessId(id);
	}
	
	
	@PutMapping(value="/editOrder")
	public PoDTO editOrder(@RequestBody PoDTO po) {
		ordersDao.editOrder(po);
		return ordersDao.getOrderByProcessId(po.getId());
	}
	
	@PutMapping(value="/editReciving")
	public ReceiptDTO editReciving(@RequestBody ReceiptDTO receipt) {
		orderRecipt.editReceipt(receipt);
		return orderRecipt.getReceiptByProcessId(receipt.getId());
	}
	
	@PutMapping(value="/editReceiveSample")
	public SampleReceiptDTO editReceiveSample(@RequestBody SampleReceiptDTO sample) {
		samples.editSampleReceipt(sample);
		return samples.getSampleReceiptByProcessId(sample.getId());
	}
	
	@PostMapping("/receiveExtra/{id}")
	public ReceiptDTO receiveExtra(@RequestBody Map<String, Map<String, List<ExtraAddedDTO>>> listChanges, @PathVariable("id") int reciptId) {
		listChanges.forEach((k, v) -> {
					orderRecipt.addExtra(v.get("extraAdded"), Integer.parseInt(k));
		});
		return orderRecipt.getReceiptByProcessId(reciptId);
	}
	
	@RequestMapping("/orderDetails/{id}")
	public PoDTO orderDetails(@PathVariable("id") int id) {
		return ordersDao.getOrderByProcessId(id);
	}
	
	@RequestMapping("/orderDetailsPo/{id}")
	public PoDTO orderDetailsPo(@PathVariable("id") int poCode) {
			return ordersDao.getOrder(poCode);
	}
	
	@RequestMapping("/receiveDetails/{id}")
	public ReceiptDTO receiveDetails(@PathVariable("id") int id) {
		return orderRecipt.getReceiptByProcessId(id);
	}
	
	@RequestMapping("/getCashewOrdersOpen")
	public List<PoItemRow> getCashewOrdersOpen() {
		return orderReports.findOpenCashewOrderItems();
	}
	
	@RequestMapping("/getGeneralOrdersOpen")
	public List<PoItemRow> getGeneralOrdersOpen() {
		return orderReports.findOpenGeneralOrderItems();
	}
	
	@RequestMapping("/getPendingCashew")
	public List<ReceiptRow> getPendingCashew() {
		return receiptReports.findPendingCashewReceipts();
	}
	
	@RequestMapping("/getPendingGeneral")
	public List<ReceiptRow> getPendingGeneral() {
		return receiptReports.findPendingGeneralReceipts();
	}
	
	@RequestMapping("/getReceivedCashew")
	public List<ReceiptRow> getReceivedCashew(@QueryParam("begin")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime begin, 
			@QueryParam("end")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
		return receiptReports.findFinalCashewReceipts(begin, end);
	}
	
	@RequestMapping("/getReceivedGeneral")
	public List<ReceiptRow> getReceivedGeneral(@QueryParam("begin")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime begin, 
			@QueryParam("end")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
		return receiptReports.findFinalGeneralReceipts(begin, end);
	}

	@RequestMapping("/getHistoryCashewOrders")
	public List<PoItemRow> getHistoryCashewOrders(@QueryParam("begin")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime begin, 
			@QueryParam("end")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
		return orderReports.findAllCashewOrderItemsHistory(begin != null? begin.toLocalDate() : null, end != null? end.toLocalDate() : null);
	}
	
	@RequestMapping("/findCashewReceiptsHistory")
	public List<ReceiptRow> findCashewReceiptsHistory(@QueryParam("begin")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime begin, 
			@QueryParam("end")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
		return receiptReports.findCashewReceiptsHistory(begin, end);
	}
	
	@RequestMapping("/findGeneralReceiptsHistory")
	public List<ReceiptRow> findGeneralReceiptsHistory(@QueryParam("begin")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime begin, 
			@QueryParam("end")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
		return receiptReports.findGeneralReceiptsHistory(begin, end);
	}
	
	@RequestMapping("/getAllCashewReciveRejected")
	public List<ReceiptRow> getAllCashewReciveRejected() {
		return orderRecipt.findCancelledCashewReceipts();
	}
	
	@RequestMapping("/getAllGeneralOrders")
	public List<PoItemRow> getAllGeneralOrders(@QueryParam("begin")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime begin, 
			@QueryParam("end")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
		return orderReports.findAllGeneralOrderItemsHistory(begin != null? begin.toLocalDate() : null, end != null? end.toLocalDate() : null);
	}
	
	@RequestMapping("/getCashewSuppliers")
	public List<DataObjectWithName<Supplier>> getCashewSuppliers() {
		return refeDao.getCashewSuppliersBasic();
	}
	
	@RequestMapping("/getGeneralSuppliers")
	public List<DataObjectWithName<Supplier>> getGeneralSuppliers() {
		return refeDao.getGeneralSuppliersBasic();
	}
	
	@RequestMapping("/getCashewPoOpen")
	public Set<PoCodeBasic> getCashewPoOpen() {
		return objectTableReader.findOpenCashewOrdersPoCodes();
	}
	
	@RequestMapping("/getPoCashewCodesOpenPending")
	public Set<PoCodeBasic> getPoCashewCodesOpenPending() {
		return objectTableReader.findOpenAndPendingCashewOrdersPoCodes();
	}
	
	@RequestMapping("/getGeneralPoOpen")
	public Set<PoCodeBasic> getGeneralPoOpen() {
		return objectTableReader.findOpenGeneralOrdersPoCodes();
	}
	
	@RequestMapping("/findFreePoCodes")
	public List<PoCodeBasic> findFreePoCodes() {
		return objectTableReader.findFreePoCodes();
	}
	
	@RequestMapping("/findAllPoCodes")
	public List<PoCodeBasicWithProductCompany> findAllPoCodes() {
		return objectTableReader.findAllProductPoCodes();
	}
	
	@RequestMapping("/getPoCode/{id}")
	public PoCodeDTO getPoCode(@PathVariable("id") int poCode) {
		return objectWriter.getPoCode(poCode);
	}
	
	@PostMapping(value="/addPoCode")
	public ResponseEntity<?> addPoCode(@RequestBody ProductPoCodeDTO poCode) {
		objectWriter.addPoCode(poCode);
		return ResponseEntity.ok().build();
	}
	
	@PutMapping(value="/editPoCode")
	public ResponseEntity<?> editPoCode(@RequestBody ProductPoCodeDTO poCode) {
		objectWriter.editPoCode(poCode);
		return ResponseEntity.ok().build();
	}
	
	@RequestMapping("/getGeneralPoCode/{id}")
	public PoCodeDTO getGeneralPoCode(@PathVariable("id") int poCode) {
		return objectWriter.getPoCode(poCode);
	}
	
	@PostMapping(value="/addGeneralPoCode")
	public ResponseEntity<?> addGeneralPoCode(@RequestBody GeneralPoCodeDTO poCode) {
		objectWriter.addPoCode(poCode);
		return ResponseEntity.ok().build();
	}
	
	@PutMapping(value="/editGeneralPoCode")
	public ResponseEntity<?> editGeneralPoCode(@RequestBody GeneralPoCodeDTO poCode) {
		objectWriter.editPoCode(poCode);
		return ResponseEntity.ok().build();
	}
	
	@RequestMapping("/findAllGeneralPoCodes")
	public List<PoCodeBasicWithProductCompany> findAllGeneralPoCodes() {
		return objectTableReader.findAllGeneralPoCodes();
	}
	
	@RequestMapping("/findFreeGeneralPoCodes")
	public List<PoCodeBasic> findFreeGeneralPoCodes() {
		return objectTableReader.findFreeGeneralPoCodes();
	}
	
	@PostMapping(value="/addMixPoCode")
	public ResponseEntity<?> addMixPoCode(@RequestBody MixPoCode poCode) {
		objectWriter.addMixPoCode(poCode);
		return ResponseEntity.ok().build();
	}
	
	@PutMapping(value="/editMixPoCode")
	public ResponseEntity<?> editMixPoCode(@RequestBody MixPoCode poCode) {
		objectWriter.editMixPoCode(poCode);
		return ResponseEntity.ok().build();
	}
	
	@RequestMapping("/getAllSuppliers")
	public List<DataObjectWithName<Supplier>> getAllSuppliers() {
		return refeDao.getSuppliersBasic();
	}
	
	
	@RequestMapping("/getGeneralContractTypes")
	public List<ContractTypeDTO> getGeneralContractTypes() {
		return refeDao.getGeneralContractTypes();
	}
	
	@RequestMapping("/getCashewContractTypes")
	public List<ContractTypeDTO> getCashewContractTypes() {
		return refeDao.getCashewContractTypes();
	}
	
	@RequestMapping("/getSuppliersGroups")
	public List<DataObjectWithName<Supplier>> getSuppliersGroups() {
		return refeDao.getSuppliersBasicByGroup(SupplyGroup.SHIPPED_PRODUCT);
	}
	
	
}
