/**
 * 
 */
package com.avc.mis.beta.entities.codes;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import com.avc.mis.beta.entities.BaseEntity;
import com.avc.mis.beta.entities.ValueInterface;
import com.avc.mis.beta.entities.process.ContainerLoading;
import com.avc.mis.beta.entities.values.ShippingPort;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Basic immutable information that serves as identification for container shipment.
 * Includes a code and destination.
 * 
 * code and id are synonymous in this class
 * 
 * @author Zvi
 *
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "SHIPMENT_CODES", uniqueConstraints = 
	{ @UniqueConstraint(columnNames = { "code", "portOfDischargeId" }) })
public class ShipmentCode extends BaseEntity implements ValueInterface {

	@NotNull(message = "code is mandatory")
	@Column(nullable = false, updatable = false)
	private String code;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "portOfDischargeId")
	@NotNull(message = "Port of discharge is mandatory")
	private ShippingPort portOfDischarge;
	
	@JsonIgnore
	@ToString.Exclude 
	@OneToMany(mappedBy = "shipmentCode", fetch = FetchType.LAZY)
	private Set<ContainerLoading> loadings = new HashSet<>();
	
	/**
	 * @return a string representing full Shipment code. e.g. TAN-51284
	 */
	public String getValue() {
		return String.format("%s-%s", this.portOfDischarge.getCode(), this.code);
	}

	
}
