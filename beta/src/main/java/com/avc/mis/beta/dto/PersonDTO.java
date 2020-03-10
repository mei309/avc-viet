/**
 * 
 */
package com.avc.mis.beta.dto;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import com.avc.mis.beta.dataobjects.CompanyPosition;
import com.avc.mis.beta.dataobjects.ContactDetails;
import com.avc.mis.beta.dataobjects.IdCard;
import com.avc.mis.beta.dataobjects.Person;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @author Zvi
 *
 */
@Data
@NoArgsConstructor
public class PersonDTO implements Serializable {
	@EqualsAndHashCode.Exclude
	private Integer id;
	private String name;
	private IdCardDTO idCard;
	private ContactDetailsDTO contactDetails;

	/**
	 * @param person
	 */
	public PersonDTO(@NonNull Person person) {
		this.id = person.getId();
		this.name = person.getName();
		if(person.getIdCard() != null)
			this.idCard = new IdCardDTO(person.getIdCard());
		if(person.getContactDetails() != null)
			this.contactDetails = new ContactDetailsDTO(person.getContactDetails());
	}

}
