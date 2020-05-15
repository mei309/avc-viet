/**
 * 
 */
package com.avc.mis.beta.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.avc.mis.beta.entities.data.BankAccount;
import com.avc.mis.beta.entities.data.Company;
import com.avc.mis.beta.entities.data.CompanyContact;
import com.avc.mis.beta.entities.data.ContactDetails;
import com.avc.mis.beta.entities.data.Person;
import com.avc.mis.beta.entities.data.ProcessAlert;
import com.avc.mis.beta.entities.data.UserEntity;
import com.avc.mis.beta.entities.process.PoCode;
import com.avc.mis.beta.repositories.ObjectTablesRepository;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Used to access full lists (tables) of active Object entities - {@link com.avc.mis.beta.entities.ObjectDataEntity}
 * Gets list for user input data to reference (usually by choosing from a list).
 * Dosen't get non active entities (soft deleted entities).
 * Object entities should be second in restore order before other types of Entities and after Value entities, 
 * ideally getters should be ordered by restore order.
 * 
 * @author Zvi
 *
 */
@Service
@Getter(value = AccessLevel.PRIVATE)
@Transactional(readOnly = true)
public class ObjectTableReader {
	
	@Autowired private ObjectTablesRepository objectTablesRepository;
	
//----------------------------Independent Objects------------------------------------------

	public List<BankAccount> getAllBankAccounts() {
		return getObjectTablesRepository().findAllBankAccounts();		
	}
		
	public List<Person> getAllPersons() {
		return getObjectTablesRepository().findAllPersons();		
	}
	
	public List<Company> getAllCompanies() {
		return getObjectTablesRepository().findAllCompanies();		
	}
	
	public List<PoCode> getAllPoCodes() {
		return getObjectTablesRepository().findAllPoCodes();		
	}
	
//-----------------------------Dependent Objects--------------------------------------------
	
	public List<CompanyContact> getAllCompanyContacts() {
		return getObjectTablesRepository().findAllCompanyContacts();		
	}
	
	public List<ProcessAlert> getAllProcessTypeAlerts() {
		return getObjectTablesRepository().findAllProcessAlerts();		
	}
	
	public List<ContactDetails> getAllContactDetails() {
		return getObjectTablesRepository().findAllContactDetails();		
	}
	
	public List<UserEntity> getAllUsers() {
		return getObjectTablesRepository().findAllUsers();		
	}
	
//----------------------------Contact Details Objects-------------------------------------------
	
	//no need to add them for now - address, email, fax, phone and payment account.
	
	
//---------------------------------DTOs---------------------------------------------------------
	
	
	
}