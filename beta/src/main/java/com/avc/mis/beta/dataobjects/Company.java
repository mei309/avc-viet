/**
 * 
 */
package com.avc.mis.beta.dataobjects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.BatchSize;
import org.springframework.beans.factory.annotation.Value;

import com.avc.mis.beta.dao.DAO;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

/**
 * @author Zvi
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name="COMPANIES", 
	uniqueConstraints = @UniqueConstraint(name = "existing compony name", columnNames = {"name"}))
@Inheritance(strategy=InheritanceType.JOINED)
public class Company implements Insertable, KeyIdentifiable {
	
	@EqualsAndHashCode.Include
	@Id @GeneratedValue	
	private Integer id;
	
	@Column(nullable = false)
	private String name;
	private String localName;
	private String englishName;
	private String license;
	private String taxCode;
	private String registrationLocation;
	
	@JsonManagedReference(value = "company_contactDetails")
	@OneToOne(mappedBy = "company", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
	private ContactDetails contactDetails;
	
	@JsonManagedReference(value = "company_companyContacts")
	@OneToMany(mappedBy = "company",cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
	@BatchSize(size = DAO.BATCH_SIZE)
	private Set<CompanyContact> companyContacts = new HashSet<>();
	

//	@ToString.Exclude
//	@Column(columnDefinition = "boolean default true", nullable = false)
//	private boolean isActive = true;
	
	public void setCompanyContacts(CompanyContact[] companyContacts) {
		this.companyContacts = Insertable.filterAndSetReference(companyContacts, 
				(t) -> {t.setReference(this);	return t;});
	}
	
	public CompanyContact[] getCompanyContacts() {
		return (CompanyContact[])this.companyContacts.toArray(new CompanyContact[this.companyContacts.size()]);
	}
	
	public void setContactDetails(ContactDetails contactDetails) {		
		if(contactDetails != null) {
			contactDetails.setReference(this);
			this.contactDetails = contactDetails;			
		}
	}
	
	public ContactDetails getContactDetails() {
		if(this.contactDetails == null) {
			setContactDetails(new ContactDetails());
		}
		return this.contactDetails;
	}
	
	public void setName(String name) {
		this.name = name.trim();
	}
	
	protected boolean canEqual(Object o) {
		return KeyIdentifiable.canEqualCheckNullId(this, o);
	}

		
	/**
	 * @return
	 */
	public boolean isLegal() {
		return StringUtils.isNotBlank(name);
	}

	/**
	 * Empty implementation
	 */
	@Override
	public void setReference(Object referenced) {}


}
