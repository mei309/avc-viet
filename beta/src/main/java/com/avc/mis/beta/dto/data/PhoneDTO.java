/**
 * 
 */
package com.avc.mis.beta.dto.data;

import com.avc.mis.beta.dto.SubjectDataDTO;
import com.avc.mis.beta.entities.BaseEntity;
import com.avc.mis.beta.entities.data.Phone;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * DTO(Data Access Object) for sending or displaying Phone entity data.
 * 
 * @author Zvi
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PhoneDTO extends SubjectDataDTO {

	private String value;
	
	public PhoneDTO(@NonNull Phone phone) {
		super(phone.getId(), phone.getVersion(), phone.getOrdinal());
		this.value = phone.getValue();
	}


	public PhoneDTO(Integer id, Integer version, String value, int ordinal) {
		super(id, version, ordinal);
		this.value = value;
	}
	
	@Override
	public Class<? extends BaseEntity> getEntityClass() {
		return Phone.class;
	}
	
	@Override
	public Phone fillEntity(Object entity) {
		Phone phoneEntity;
		if(entity instanceof Phone) {
			phoneEntity = (Phone) entity;
		}
		else {
			throw new IllegalStateException("Param has to be Phone class");
		}
		super.fillEntity(phoneEntity);
		phoneEntity.setValue(getValue());;
		return phoneEntity;
	}
}
