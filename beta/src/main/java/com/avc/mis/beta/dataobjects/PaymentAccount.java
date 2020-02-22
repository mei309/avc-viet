/**
 * 
 */
package com.avc.mis.beta.dataobjects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import com.avc.mis.beta.dao.services.PreparedStatementCreatorImpl;
import com.fasterxml.jackson.annotation.JsonBackReference;

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
@Entity
@Table(name="PAYMENT_ACCOUNTS")
public class PaymentAccount {
	
	@Id @GeneratedValue
	private int id;
	
//	@Column(name="contactId", nullable = false)
//	private int contactId;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonBackReference(value = "contactDetails_paymentAccount")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contactId", updatable = false)
	private ContactDetails contactDetails;
	
	@JoinTable(name = "BANK_PAYEES", 
			joinColumns = @JoinColumn(name="paymentId", referencedColumnName="id", nullable = false),
			inverseJoinColumns = @JoinColumn(name = "accountId",referencedColumnName = "id", nullable = false))
	@ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
	private BankAccount bankAccount;
	
	
	
	/**
	 * @param jdbcTemplateObject
	 * @param paymentAccounts
	 */
	public static void insertPaymentAccounts(JdbcTemplate jdbcTemplateObject, 
			int contactId, PaymentAccount[] paymentAccounts) {
		
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		String sql = "insert into PAYMENT_ACCOUNTS (contactId) values (?)";
		PreparedStatementCreatorImpl psc = new PreparedStatementCreatorImpl(
				sql, new Object[] {contactId}, new String[] {"id"});

		BankAccount bankAccount;
		int paymentAccountId;
		for(PaymentAccount paymentAccount: paymentAccounts) {			
			
			bankAccount = paymentAccount.getBankAccount();			
			if(bankAccount != null && bankAccount.getAccountNo() != null &&
					bankAccount.getOwnerName() != null && bankAccount.getBranch() != null && 
					bankAccount.getBranch().getId() != null) {
				jdbcTemplateObject.update(psc, keyHolder);
				paymentAccountId = keyHolder.getKey().intValue();
				paymentAccount.setId(paymentAccountId);
				bankAccount.insertBankAccount(jdbcTemplateObject, paymentAccountId, bankAccount);
			}
		}

		
	}	
}
