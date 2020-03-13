/**
 * 
 */
package com.avc.mis.beta.entities.data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.avc.mis.beta.entities.interfaces.Insertable;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Zvi
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name="PAYMENT_ACCOUNTS")
public class PaymentAccount implements Insertable {
	
	@EqualsAndHashCode.Include
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ToString.Exclude
	@JsonBackReference(value = "contactDetails_paymentAccount")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contactId", updatable = false)
	private ContactDetails contactDetails;
	
	@JoinTable(name = "BANK_PAYEES", 
			joinColumns = @JoinColumn(name="paymentId", referencedColumnName="id"),
			inverseJoinColumns = @JoinColumn(name = "accountId",referencedColumnName = "id"))
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
	private BankAccount bankAccount;
	
	protected boolean canEqual(Object o) {
		return Insertable.canEqualCheckNullId(this, o);
	}
	
//	@ToString.Exclude
//	@Column(nullable = false)
//	private RecordStatus status = RecordStatus.ACTIVE;
	
	/**
	 * @return
	 */
	@JsonIgnore
	@Override
	public boolean isLegal() {
		return getBankAccount() != null && getBankAccount().isLegal();
	}
	
	@PrePersist @PreUpdate
	@Override
	public void prePersistOrUpdate() {
		if(!isLegal())
			throw new IllegalArgumentException("Payment info not legal\n "
					+ "Account has to have a legal bank account");
	}
	
	@Override
	public void setReference(Object referenced) {
		this.setContactDetails((ContactDetails)referenced);
		
	}
}