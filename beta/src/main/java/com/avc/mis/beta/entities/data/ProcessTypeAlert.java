/**
 * 
 */
package com.avc.mis.beta.entities.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avc.mis.beta.entities.LinkEntity;
import com.avc.mis.beta.entities.enums.ApprovalType;
import com.avc.mis.beta.entities.values.ProcessType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Zvi
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity
@Table(name = "PROCESS_ALERTS")
public class ProcessTypeAlert extends LinkEntity {

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "processTypeId", nullable = false, updatable = false)
	private ProcessType processType;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId", nullable = false, updatable = false)
	private UserEntity user;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ApprovalType approvalType;

	@JsonIgnore
	@Override
	public boolean isLegal() {
		return processType != null && user != null && approvalType != null;
	}

	@JsonIgnore
	@Override
	public String getIllegalMessage() {
		return "Process alert needs to specify: process type, user(staff) and approval type";
	}

}