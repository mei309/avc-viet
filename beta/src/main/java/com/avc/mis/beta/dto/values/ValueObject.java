/**
 * 
 */
package com.avc.mis.beta.dto.values;

import com.avc.mis.beta.dto.ValueDTO;

import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * Generic class for holding objects with an id of of it's owner object.
 * Used where we need to reference lists of objects, where each references the id of it's wrapping object.
 * 
 * @author Zvi
 *
 */
@Value
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class ValueObject<T> extends ValueDTO {

	T value;

	public ValueObject(Integer id, T value) {
		super(id);
		this.value = value;
	}
}
