/**
 * 
 */
package com.avc.mis.beta.dto.process;

import java.util.List;
import java.util.stream.Collectors;

import com.avc.mis.beta.dto.process.collectionItems.CashewItemQualityDTO;
import com.avc.mis.beta.dto.process.group.ProcessItemDTO;
import com.avc.mis.beta.dto.process.info.QualityCheckInfo;
import com.avc.mis.beta.entities.BaseEntity;
import com.avc.mis.beta.entities.Ordinal;
import com.avc.mis.beta.entities.process.QualityCheck;
import com.avc.mis.beta.entities.process.collectionItems.CashewItemQuality;
import com.avc.mis.beta.entities.process.group.ProcessItem;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO(Data Access Object) for sending or displaying QualityCheck entity data.
 * 
 * @author Zvi
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class QualityCheckDTO extends ProcessWithProductDTO<ProcessItemDTO> {

	private String checkedBy;
	
	private String inspector;
	private String sampleTaker;
		
	private List<CashewItemQualityDTO> testedItems;
		
	public void setQualityCheckInfo(QualityCheckInfo info) {
		this.checkedBy = info.getCheckedBy();
		this.inspector = info.getInspector();
		this.sampleTaker = info.getSampleTaker();
	}
		
	@Override
	public void setProcessItems(List<ProcessItemDTO> processItems) {
		super.setProcessItems(processItems);
	}
		
	@Override
	public Class<? extends BaseEntity> getEntityClass() {
		return QualityCheck.class;
	}
	
	@Override
	public QualityCheck fillEntity(Object entity) {
		QualityCheck check;
		if(entity instanceof QualityCheck) {
			check = (QualityCheck) entity;
		}
		else {
			throw new IllegalStateException("Param has to be QualityCheck class");
		}
		super.fillEntity(check);
		
		check.setCheckedBy(getCheckedBy());
		check.setInspector(getInspector());
		check.setSampleTaker(getSampleTaker());
				
		if(getTestedItems() == null || getTestedItems().isEmpty()) {
			throw new IllegalArgumentException("Quality check has to contain at least one testsed item");
		}
		else {
			Ordinal.setOrdinals(getTestedItems());
			check.setTestedItems(getTestedItems().stream().map(i -> i.fillEntity(new CashewItemQuality())).collect(Collectors.toSet()));
		}
		if(getProcessItems() != null) {
			Ordinal.setOrdinals(getProcessItems());
			check.setProcessItems(getProcessItems().stream().map(i -> i.fillEntity(new ProcessItem())).collect(Collectors.toSet()));
		}
		
		return check;
	}
	
	
}
