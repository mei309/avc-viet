/**
 * 
 */
package com.avc.mis.beta.entities.values;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.avc.mis.beta.entities.ValueEntity;
import com.avc.mis.beta.entities.enums.ProcessName;
import com.avc.mis.beta.entities.link.ProcessManagement;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Process type entity, used to distinguish between different types of process 
 * and record their specific requirements.
 * 
 * @author Zvi
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity
@Table(name="PROCESS_TYPES")
public class ProcessType extends ValueEntity {

	@JsonIgnore
	@Enumerated(EnumType.STRING)
	@Column(name = "processName", unique = true, nullable = false)
	@NotNull(message = "Process type has to have a unique name(value)")
	private ProcessName processName;

	@JsonIgnore
	@OneToMany(mappedBy = "processType", fetch = FetchType.LAZY)
	private Set<ProcessManagement> alertRequirments = new HashSet<>();

}
