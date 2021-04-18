/**
 * 
 */
package com.avc.mis.beta.serviceinterface;

import java.util.List;

import com.avc.mis.beta.dto.process.ProductionProcessDTO;
import com.avc.mis.beta.dto.report.ProductionReportLine;
import com.avc.mis.beta.dto.view.ProcessRow;
import com.avc.mis.beta.entities.enums.ProcessName;
import com.avc.mis.beta.entities.process.ProductionProcess;

/**
 * @author zvi
 *
 */
public interface ProductionProcessService {

	/**
	 * Gets All processes for the given ProcessName
	 * @param processName the process name. e.g. CASHEW_CLEANING
	 * @return List of ProcessRow for given type of process
	 */
	public List<ProcessRow> getProductionProcessesByType(ProcessName processName);
	
	/**
	 * Gets processes done for a given PO Code and ProcessName
	 * @param processName the process name. e.g. CASHEW_CLEANING
	 * @param poCodeId id of PO Code, to fetch processes that process the given PO Code.
	 * If poCodeId argument is null, so will get a list of all process of the given type.
	 * In case of a mixed process will only show the given PO Code in the process row.
	 * @return List of ProcessRow for processes that match the given arguments.
	 */
	public List<ProcessRow> getProductionProcessesByTypeAndPoCode(ProcessName processName, Integer poCodeId);
	
	/**
	 * Summary of Production for a given PO Code and ProcessName,
	 * Used for Final Report
	 * @param processName the process name. e.g. CASHEW_CLEANING
	 * @param poCodeId poCodeId id of PO Code, to fetch processes that process the given PO Code.
	 * @return ProductionReportLine containing summary of all productions for the given arguments.
	 */
	public ProductionReportLine getProductionSummary(ProcessName processName, Integer poCodeId);
	
	/**
	 * Add/Insert/Persist a new ProductionProcess to the Persistence context.
	 * @param process the new ProductionProcess entity
	 * @param processName the name of the process type to be added. (CASHEW_CLEANING\CASHEW_ROASTING\PACKING)
	 */
	public void addProductionProcess(ProductionProcess process, ProcessName processName);
	
	/**
	 * Get the full Production Process information
	 * @param processId id of the ProductionProcess
	 * @return ProductionProcessDTO
	 */
	public ProductionProcessDTO getProductionProcess(int processId);
	
	/**
	 * Edit/Change a persisted ProductionProcess to the given ProductionProcess data
	 * @param process ProductionProcess with underlying database id and updated data.
	 */
	public void editProductionProcess(ProductionProcess process);
	
	
}
