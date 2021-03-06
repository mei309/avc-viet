/**
 * 
 */
package com.avc.mis.beta.entities.process.group;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.avc.mis.beta.entities.Insertable;
import com.avc.mis.beta.entities.Ordinal;
import com.avc.mis.beta.entities.ProcessInfoEntity;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.entities.process.collectionItems.ItemWeight;
import com.avc.mis.beta.entities.values.Item;
import com.avc.mis.beta.validation.groups.OnPersist;

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
@Table(name = "SAMPLE_ITEMS")
@Deprecated
public class SampleItem extends ProcessInfoEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "itemId", updatable = false, nullable = false)
	@NotNull(message = "Item is mandatory", groups = OnPersist.class)
	private Item item;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@NotNull(message = "Sample item measure unit is mandatory")
	private MeasureUnit measureUnit;
	
	@Column(nullable = false, precision = 19, scale = MeasureUnit.SCALE)
	@NotNull(message = "Empty container avarage weight is mandatory")
	private BigDecimal sampleContainerWeight;
		
	@OneToMany(mappedBy = "sampleItem", orphanRemoval = true, 
		cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.LAZY)
	@NotEmpty(message = "Sample item requires at least one item weight")
	private Set<ItemWeight> itemWeights = new HashSet<>();
	
	public ItemWeight[] getItemWeights() {
		ItemWeight[] itemWeights = (ItemWeight[])this.itemWeights.toArray(new ItemWeight[this.itemWeights.size()]);
		Arrays.sort(itemWeights, Ordinal.ordinalComparator());
		return itemWeights;
	}

	public void setItemWeights(Set<ItemWeight> itemWeights) {
		this.itemWeights = Insertable.setReferences(itemWeights, (t) -> {t.setReference(this);	return t;});
	}

}
