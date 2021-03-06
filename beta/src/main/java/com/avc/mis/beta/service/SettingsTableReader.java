/**
 * 
 */
package com.avc.mis.beta.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.avc.mis.beta.entities.enums.SequenceIdentifier;
import com.avc.mis.beta.entities.settings.UOM;
import com.avc.mis.beta.repositories.SettingsRepository;
import com.avc.mis.beta.utilities.ProgramSequence;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * @author Zvi
 *
 */
@Service
@Getter(value = AccessLevel.PRIVATE)
@Transactional(readOnly = true)
public class SettingsTableReader {

	@Autowired private SettingsRepository settingsRepository;

	public List<UOM> getAllUOM() {
		return getSettingsRepository().findAllUOM();
	}	
	
	/**
	 * For generating sequences e.g. for ids, codes of table
	 * @param sequenceIdentifier
	 * @return persistent ProgramSequence entity to use value and manipulate
	 */
	public ProgramSequence getSequnce(SequenceIdentifier sequenceIdentifier) {
		return getSettingsRepository().findSequence(sequenceIdentifier);
	}

	
}
