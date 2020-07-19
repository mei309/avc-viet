/**
 * 
 */
package com.avc.mis.beta.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.avc.mis.beta.dao.DeletableDAO;
import com.avc.mis.beta.dao.ProcessInfoDAO;
import com.avc.mis.beta.dto.process.PoDTO;
import com.avc.mis.beta.dto.report.PoItemRow;
import com.avc.mis.beta.dto.report.PoRow;
import com.avc.mis.beta.entities.embeddable.AmountWithUnit;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.enums.ProcessName;
import com.avc.mis.beta.entities.process.PO;
import com.avc.mis.beta.repositories.PORepository;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Service for accessing and manipulating purchase orders.
 * 
 * @author Zvi
 *
 */
@Service
@Getter(value = AccessLevel.PRIVATE)
@Transactional(readOnly = true)
public class Orders {
	
	@Autowired private ProcessInfoDAO dao;
	
	@Deprecated
	@Autowired private DeletableDAO deletableDAO;
	
	@Autowired private PORepository poRepository;	
	
	/**
	 * Gat all cashew orders with the order status - pending, received, rescheduled or cancelled.
	 * @return  list of PoRow for all orders
	 */
	public List<PoItemRow> findAllCashewOrders() {
		List<PoItemRow> itemRows = getPoRepository().findAllOrdersByType(ProcessName.CASHEW_ORDER);
		return itemRows;
	}
	
	/**
	 * Get the table of all Cashew purchase orders that are active and where not received.
	 * @return list of PoRow for orders that are yet to be received
	 */
	public List<PoRow> findOpenCashewOrders() {
		List<PoItemRow> itemRows = getPoRepository().findOpenOrdersByType(ProcessName.CASHEW_ORDER);
		Map<Integer, List<PoItemRow>> poMap = itemRows.stream()
				.collect(Collectors.groupingBy(PoItemRow::getId, Collectors.toList()));
		List<PoRow> poRows = new ArrayList<PoRow>();
		poMap.forEach((k, v) -> {
			AmountWithUnit totalAmount = v.stream()
					.map(pi -> pi.getNumberUnits())
					.reduce(new AmountWithUnit(BigDecimal.ZERO, MeasureUnit.LOT), AmountWithUnit::add);
			LocalDate deliveryDate = v.stream().findAny().get().getDeliveryDate();
			PoRow poRow = new PoRow(k, deliveryDate, totalAmount, v);
			poRows.add(poRow);
		});
		
		//sort by delivery date
		poRows.sort(new Comparator<PoRow>() {

			@Override
			public int compare(PoRow o1, PoRow o2) {
				return o1.getDeliveryDate().compareTo(o2.getDeliveryDate());
			}
		});
		return poRows;
	}
	
	public List<PoItemRow> findOpenCashewOrderItems() {
		return getPoRepository().findOpenOrdersByType(ProcessName.CASHEW_ORDER);
	}
	
	/**
	 * Get the table of all General purchase orders that are active and where not received.
	 * @return list of PoRow for orders that are yet to be received
	 */
	public List<PoItemRow> findOpenGeneralOrders() {
		return getPoRepository().findOpenOrdersByType(ProcessName.GENERAL_ORDER);
	}
	
	
	
//	/**
//	 * Gets the table of all Cashew Orders with given OrderStatus
//	 * @param statuses OrderItemStatus of the requested order items (lines)
//	 * @return List of PoRow for all orders the are in one of the given statuses.
//	 */
//	public List<PoRow> findCashewOrders(OrderItemStatus[] statuses) {
//		return getPoRepository().findByOrderTypeAndItemStatuses(ProcessName.CASHEW_ORDER, statuses);
//	}
//	
//	/**
//	 * Gets the table of all General Orders with given OrderStatus
//	 * @param statuses OrderStatuses of the requested orders
//	 * @return List of PoRow for all orders the are in one of the given statuses.
//	 */
//	public List<PoRow> findGeneralOrders(OrderStatus[] statuses) {
//		return getPoRepository().findByOrderTypeAndStatuses(ProcessName.GENERAL_ORDER, statuses);
//	}
//	
//	/**
//	 * Gets the basic information of all Cashew Orders with given OrderStatus - id, poCode, supplier and orderStatus.
//	 * @param statuses OrderItemStatus of the requested order items
//	 * @return List of PoRow for all orders the are in one of the given statuses.
//	 */
//	public List<PoBasic> findCashewOrdersBasic(OrderItemStatus[] statuses) {
//		return getPoRepository().findByOrderTypeAndItemStatusesBasic(ProcessName.CASHEW_ORDER, statuses);		
//	}
//	
//	/**
//	 * Gets the basic information of all General Orders with given OrderStatus - id, poCode, supplier and orderStatus.
//	 * @param statuses OrderStatuses of the requested orders
//	 * @return List of PoRow for all orders the are in one of the given statuses.
//	 */
//	public List<PoBasic> findGeneralOrdersBasic(OrderStatus[] statuses) {
//		return getPoRepository().findByOrderTypeAndStatusesBasic(ProcessName.GENERAL_ORDER, statuses);
//	}
	
	@Transactional(rollbackFor = Throwable.class, readOnly = false)
	private void addOrder(PO po) {
		//using save rather than persist in case POid was assigned by user
		dao.addEntityWithFlexibleGenerator(po.getPoCode());
//		Session session = getEntityManager().unwrap(Session.class);
//		session.save(po.getPoCode());
		dao.addProcessEntity(po);			
	}

	/**
	 * Adds a new Cashew purchase order
	 * @param po Cashew purchase order with all required details
	 * @throws IllegalArgumentException if supplier or order items aren't set.
	 */
	@Transactional(rollbackFor = Throwable.class, readOnly = false)
	public void addCashewOrder(PO po) {
		po.setProcessType(dao.getProcessTypeByValue(ProcessName.CASHEW_ORDER));
		addOrder(po);		
	}
	
	/**
	 * Adds a new General purchase order
	 * @param po General purchase order with all required details
	 * @throws IllegalArgumentException if supplier or order items aren't set.
	 */
	@Transactional(rollbackFor = Throwable.class, readOnly = false)
	public void addGeneralOrder(PO po) {		
		po.setProcessType(dao.getProcessTypeByValue(ProcessName.GENERAL_ORDER));
		addOrder(po);	
	}
	
	/**
	 * Gets full details of purchase order with given poCode id. 
	 * @param poCode the id number of the poCode
	 * @return PoDTO object with purchase order details
	 * @throws IllegalArgumentException if purchase order for given poCode dosen't exist.
	 */
	public PoDTO getOrder(int poCode) {
		Optional<PoDTO> order = getPoRepository().findOrderByPoCodeId(poCode);
		PoDTO po = order.orElseThrow(
				()->new IllegalArgumentException("No order with given PO code"));
		po.setOrderItems(getPoRepository().findOrderItemsByPo(po.getId()));
		
		return po;
	}
	
	/**
	 * Gets full details of purchase order with given ProductionProcess id. 
	 * @param processId the id of ProductionProcess requested
	 * @return PoDTO object with purchase order details
	 * @throws IllegalArgumentException if purchase order for given process id dosen't exist.
	 */
	public PoDTO getOrderByProcessId(int processId) {
		Optional<PoDTO> order = getPoRepository().findOrderByProcessId(processId);
		PoDTO poDTO = order.orElseThrow(
				()->new IllegalArgumentException("No order with given process id"));
		poDTO.setOrderItems(getPoRepository().findOrderItemsByPo(poDTO.getId()));
		
		return poDTO;
	}
	
	/**
	 * Update the given PO with the set data - OrderStatus, ProcessStatus, PO items and remarks.
	 * Ignores changed non editable fields.
	 * @param po PO updated with edited state
	 */
	@Transactional(rollbackFor = Throwable.class, readOnly = false)
	public void editOrder(PO po) {
		dao.editProcessEntity(po);
	}
	
	
	@Transactional(rollbackFor = Throwable.class, readOnly = false)
	@Deprecated
	public void removeOrder(int orderId) {
		getDeletableDAO().permenentlyRemoveEntity(PO.class, orderId);
	}
	
}
