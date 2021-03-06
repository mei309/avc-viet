/**
 * 
 */
package com.avc.mis.beta.dto;

import com.avc.mis.beta.entities.DataEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO for non entities. e.g. view, query and reports.
 * Contain a version to be used for reference in persistence context.
 * Not inserted by users, therefore id won't be null.
 * Will typically (also) compare ids for comparing 2 objects of the same class.
 * 
 * @author zvi
 *
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public abstract class BasicDataValueDTO extends BasicValueDTO {

	private Integer version;
	
	public BasicDataValueDTO(Integer id, Integer version) {
		super(id);
		this.version = version;
	}
	
	@Override
	public DataEntity fillEntity(Object entity) {
		DataEntity dataEntity;
		if(entity instanceof DataEntity) {
			dataEntity = (DataEntity) entity;
		}
		else {
			throw new IllegalStateException("Param has to be DataEntity class");
		}
		super.fillEntity(dataEntity);
		dataEntity.setVersion(getVersion());
		
		return dataEntity;
	}

}
