/**
 * 
 */
package com.avc.mis.beta.entities;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Zvi
 *
 */
@MappedSuperclass
public abstract class BaseEntityNoId implements Insertable {
	
}
