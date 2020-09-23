/**
 * 
 */
package com.avc.mis.beta.entities;

import javax.persistence.MappedSuperclass;

/**
 * Abstract base class extended by all persistence entities.
 * 
 * @author Zvi
 *
 */
@MappedSuperclass
public abstract class BaseEntity implements Insertable {

	/**
	 * Defined so that new Entities with the same data aren't considered equals before they are assigned an id,
	 * so we can assign sets of the entity that have the same data.
	 * After persisting it's enough to only compare their id.
	 * @param o
	 * @return false if both this object's and given object's id is null 
	 * or given object is not a subclass of BaseEntity, otherwise returns true.
	 */
	@Override 
	public boolean equals(Object o) {
	    if (o == this) return true;
	    if (!(o instanceof BaseEntity)) return false;
	    BaseEntity other = (BaseEntity) o;
	    
	    return !(this.getId() == null && other.getId() == null);
	}
	  
	/**
	 * Override Object class default, 
	 * so equal items have the same hashcode.
	 * e.g. two entities with same id other than null.
	 */
	@Override 
	public int hashCode() {
	    final int PRIME = 59;
		return PRIME;
	}
}
