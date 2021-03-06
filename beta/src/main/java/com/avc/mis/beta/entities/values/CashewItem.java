/**
 * 
 */
package com.avc.mis.beta.entities.values;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Positive;

import com.avc.mis.beta.entities.enums.SaltLevel;
import com.avc.mis.beta.validation.groups.PositiveAmount;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Cashew item - item with additional cashew properties.
 * 
 * @author zvi
 *
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity
@DiscriminatorValue("cashew")
public class CashewItem extends Item {
	
	@Column(columnDefinition = "int default 1")
	@Positive(message = "Number of bags has to be positive", groups = PositiveAmount.class)
	private int numBags = 1;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "gradeId")
	private CashewGrade grade;

	@Column(columnDefinition = "boolean not null default 0")
	private boolean whole = false;

	@Column(columnDefinition = "boolean not null default 0")
	private boolean roast = false;

	@Column(columnDefinition = "boolean not null default 0")
	private boolean toffee = false;

	@Enumerated(EnumType.STRING)
	private SaltLevel saltLevel = SaltLevel.NS;

}
