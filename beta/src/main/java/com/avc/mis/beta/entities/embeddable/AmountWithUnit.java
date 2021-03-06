/**
 * 
 */
package com.avc.mis.beta.entities.embeddable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.avc.mis.beta.dto.basic.ItemWithUnitDTO;
import com.avc.mis.beta.entities.enums.ItemGroup;
import com.avc.mis.beta.entities.enums.MeasureUnit;
import com.avc.mis.beta.validation.groups.PositiveAmount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Amount with measure unit. 
 * e.g. 5kg, 100units etc.
 * 
 * @author Zvi
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class AmountWithUnit implements Cloneable {
		
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###.######");

	public static final AmountWithUnit ZERO_KG = new AmountWithUnit(BigDecimal.ZERO, MeasureUnit.KG);
	public static final AmountWithUnit ZERO_LOT = new AmountWithUnit(BigDecimal.ZERO, MeasureUnit.LOT);
	public static final AmountWithUnit ZERO_LBS = new AmountWithUnit(BigDecimal.ZERO, MeasureUnit.LBS);
	public static final AmountWithUnit ONE_UNIT = new AmountWithUnit(BigDecimal.ONE, MeasureUnit.UNIT);
	public static final AmountWithUnit NEUTRAL = new AmountWithUnit(BigDecimal.ONE, MeasureUnit.NONE);;


//	@NotNull(message = "Amount is required") -- now could be null, null regarded as 1.
	@Positive(message = "Amount has to be positive", groups = PositiveAmount.class)
	private BigDecimal amount; // = BigDecimal.ONE;
	
	@Enumerated(EnumType.STRING)
	@NotNull(message = "Measure unit required")
	private MeasureUnit measureUnit;
	
	
	public AmountWithUnit(BigDecimal amount, String measureUnit) {
		super();
		this.amount = amount;
		this.measureUnit = MeasureUnit.valueOf(measureUnit);
	}
	
	public AmountWithUnit(BigInteger amount, MeasureUnit measureUnit) {
		super();
		this.amount = new BigDecimal(amount);
		this.measureUnit = measureUnit;
	}
	
	public AmountWithUnit(String amount, String measureUnit) {
		super();
		this.amount = new BigDecimal(amount);
		this.measureUnit = MeasureUnit.valueOf(measureUnit);
	}
	
	public AmountWithUnit(MeasureUnit measureUnit) {
		super();
		this.measureUnit = measureUnit;
	}
	
	public BigDecimal getAmount() {
		return this.amount != null ? this.amount : BigDecimal.ONE;
	}
	
	public AmountWithUnit add(AmountWithUnit augend) {
		return add(augend.getAmount(), augend.getMeasureUnit());
	}
	
	public AmountWithUnit add(BigDecimal augendAmount, MeasureUnit augendMeasureUnit) {
		BigDecimal augendConvertedAmount = MeasureUnit.convert(augendAmount, augendMeasureUnit, this.measureUnit);
		if(augendConvertedAmount == null) {
			throw new UnsupportedOperationException(
					"Convertion from " + augendMeasureUnit + " to " + this.measureUnit + " not supported");
		}
		return new AmountWithUnit(getAmount().add(augendConvertedAmount), this.measureUnit);
	}
	
	private AmountWithUnit negate() {
		return new AmountWithUnit(getAmount().negate(), measureUnit);
	}
	
	public AmountWithUnit subtract(AmountWithUnit subtrahend) {
		BigDecimal subtrahendAmount = MeasureUnit.convert(subtrahend.getAmount(), subtrahend.getMeasureUnit(), this.measureUnit);
		if(subtrahendAmount == null)
			throw new UnsupportedOperationException(
					"Convertion from " + subtrahend.getMeasureUnit() + " to " + this.measureUnit + " not supported");
		return new AmountWithUnit(getAmount().subtract(subtrahendAmount), this.measureUnit);
	}
	
	public AmountWithUnit subtract(BigDecimal subtrahend) {
		return new AmountWithUnit(getAmount().subtract(subtrahend), this.measureUnit);
	}
	
	public AmountWithUnit convert(MeasureUnit measureUnit) {
		BigDecimal convertedAmount = MeasureUnit.convert(getAmount(), this.measureUnit, measureUnit);
		if(convertedAmount == null)
			throw new UnsupportedOperationException(
					"Convertion from " + this.measureUnit + " to " + measureUnit + " not supported");
		return new AmountWithUnit(convertedAmount, measureUnit);
	}
	
	public AmountWithUnit multiply(BigDecimal multiplicand) {
		return new AmountWithUnit(getAmount().multiply(multiplicand, MathContext.DECIMAL64), this.measureUnit);
	}
			
	@Override
	public AmountWithUnit clone() {
		return new AmountWithUnit(this.amount, this.measureUnit);
	}
	
	public AmountWithUnit setScale(int newScale) {
		if(this.amount != null) {
			return new AmountWithUnit(getAmount().setScale(newScale, RoundingMode.HALF_DOWN), this.measureUnit);
		}
		else {
			return this.clone();
		}
	}
	
	public String getValue() {
		if(this.measureUnit == null) {
			return null;
		}
		else if(this.amount != null) {
			return String.format("%s %s", 
					DECIMAL_FORMAT.format(getAmount()), 
					this.measureUnit);
		}
		else {
			return this.measureUnit.toString();
		}
	}
	
	
	public int signum() {
		return getAmount().signum();
	}

	/**
	 * @param amountsWithUnit
	 * @param scale
	 */
	public static void setScales(AmountWithUnit[] amountsWithUnit, int newScale) {
		for(int i=0; i< amountsWithUnit.length; i++) {
			amountsWithUnit[i].setScale(newScale);
		}
		
	}
	
	public static void setScales(List<AmountWithUnit> amountsWithUnit, int newScale) {
		for(AmountWithUnit i: amountsWithUnit) {
			i.setScale(newScale);
		}		
	}

	public static AmountWithUnit addNullable(AmountWithUnit base, AmountWithUnit augend) {
		if(base != null && augend != null) {
			return base.add(augend);
		}
		if(base != null) {
			return base;
		}
		if(augend != null) {
			return augend;
		}
		return null;
	}
	
	public static AmountWithUnit subtractNullable(AmountWithUnit base, AmountWithUnit subtrahend) {
		if(base != null && subtrahend != null) {
			return base.subtract(subtrahend);
		}
		if(base != null) {
			return base;
		}
		if(subtrahend != null) {
			return subtrahend.negate();
		}
		return null;
	}
	
	public BigDecimal divide(AmountWithUnit denominator) {
		return divide(this, denominator);
	}


	public static BigDecimal divide(AmountWithUnit numerator, AmountWithUnit denominator) {
		if(numerator == null || denominator == null || denominator.getAmount().equals(BigDecimal.ZERO)) {
			return null;
		}
		BigDecimal denominatorAmount = MeasureUnit.convert(denominator, numerator.getMeasureUnit());
		if(denominatorAmount == null)
			throw new UnsupportedOperationException(
					"Convertion from " + denominator.getMeasureUnit() + " to " + numerator.getMeasureUnit() + " not supported");
		return numerator.getAmount().divide(denominatorAmount, MathContext.DECIMAL64);
	}
	
	public AmountWithUnit divide(BigDecimal denominator) {
		return new AmountWithUnit(getAmount().divide(denominator, MathContext.DECIMAL64), getMeasureUnit());
	}

	public static BigDecimal percentageLoss(AmountWithUnit out, AmountWithUnit in) {
		BigDecimal ratio = AmountWithUnit.divide(out, in);
		if(ratio == null)
			return null;
		return ratio
				.setScale(MeasureUnit.DIVISION_SCALE, RoundingMode.HALF_DOWN)
				.subtract(BigDecimal.ONE)
				.multiply(new BigDecimal("100"));
	}
	
	public static Optional<AmountWithUnit> convert(@NonNull AmountWithUnit units, @NonNull MeasureUnit toUnit) {
		BigDecimal amount = MeasureUnit.convert(units.getAmount(), units.getMeasureUnit(), toUnit);
		if(amount == null) {
			return Optional.empty();
		}
		return Optional.of(new AmountWithUnit(amount, toUnit));
	}
	
	public static List<AmountWithUnit> amountDisplay(AmountWithUnit units, ItemWithUnitDTO item , List<MeasureUnit> displayMeasureUnits) {
		List<AmountWithUnit> amounts = new ArrayList<>();
		if(item.getGroup()  == ItemGroup.PRODUCT && 
				displayMeasureUnits != null && !displayMeasureUnits.isEmpty()) {
			displayMeasureUnits.forEach(mu -> {
				AmountWithUnit.convert(units, mu).ifPresent(e -> amounts.add(e));
				if(MeasureUnit.NONE != item.getUnit().getMeasureUnit()) {
					convert(item.getUnit().multiply(units.getAmount()), mu).ifPresent(e -> amounts.add(e));
				}
			});
		}
		else {
			amounts.add(units);
			if(MeasureUnit.NONE != item.getUnit().getMeasureUnit()) {
				amounts.add(item.getUnit().multiply(units.getAmount()));
			}
		}
		AmountWithUnit.setScales(amounts, MeasureUnit.SUM_DISPLAY_SCALE);
		return amounts;
	}
	
	public static List<AmountWithUnit> weightDisplay(AmountWithUnit units, List<MeasureUnit> displayMeasureUnits) {
		List<AmountWithUnit> weights = new ArrayList<>();
		if(displayMeasureUnits != null && !displayMeasureUnits.isEmpty()) {
			displayMeasureUnits.forEach(mu -> {
				AmountWithUnit.convert(units, mu).ifPresent(e -> weights.add(e));
			});
		}
		if(weights.isEmpty()) {
			weights.add(units);
		}
		AmountWithUnit.setScales(weights, MeasureUnit.SUM_DISPLAY_SCALE);
		return weights;
	}

	



}
