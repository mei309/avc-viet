/**
 * 
 */
package com.avc.mis.beta.dto;

import com.avc.mis.beta.entities.Ordinal;
import com.avc.mis.beta.entities.RankedAuditedEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO for entities that can be edited by multiple users and owned by an entity as part of a collection.
 * Contains an ordinal number for indicating priority between multiple entities of the same class, 
 * owned by the same object.
 * 
 * 
 * @author Zvi
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public abstract class RankedAuditedDTO extends DataDTO implements Ordinal {

	@EqualsAndHashCode.Exclude
	private Integer ordinal;
	
	public RankedAuditedDTO(Integer id, Integer version, Integer ordinal) {
		super(id, version);
		this.ordinal = ordinal;
	}
	
	public RankedAuditedDTO(RankedAuditedEntity entity) {
		super(entity);
		this.ordinal = entity.getOrdinal();
	}
	
	@Override
	public RankedAuditedEntity fillEntity(Object entity) {
		RankedAuditedEntity rankedAuditedEntity;
		if(entity instanceof RankedAuditedEntity) {
			rankedAuditedEntity = (RankedAuditedEntity) entity;
		}
		else {
			throw new IllegalStateException("Param has to be RankedAuditedEntity class");
		}
		super.fillEntity(rankedAuditedEntity);
		rankedAuditedEntity.setOrdinal(getOrdinal());
		return rankedAuditedEntity;
	}
	
}
