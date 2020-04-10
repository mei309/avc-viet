/**
 * 
 */
package com.avc.mis.beta.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO for entities that represent process recordings who record auditing data.
 * 
 * @author Zvi
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public abstract class ProcessDTO extends DataDTO {

	public ProcessDTO(Integer id, Long version) {
		super(id, version);
	}
}
