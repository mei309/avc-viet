/**
 * 
 */
package com.avc.mis.beta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO for entities that can be edited by multiple users.
 * contains a version.
 * 
 * @author Zvi
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public abstract class DataDTO extends BaseDTO {
	
	private Integer version;
	
	public DataDTO(Integer id, Integer version) {
		super(id);
		this.version = version;
	}
}
