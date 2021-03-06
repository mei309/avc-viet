/**
 * 
 */
package com.avc.mis.beta.utilities;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Converts a list of OrdinalAmounts to one String and vice versa, for recording and fetching from database respectively.
 * 
 * @author zvi
 *
 */
@Converter
public class OrdinalAmountsListToString  implements AttributeConverter<List<OrdinalAmount<BigDecimal>>, String> {

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(List<OrdinalAmount<BigDecimal>> list) {
		if(list == null) {
			return null;
		}
		if(list.stream().anyMatch(i -> i.getAmount() == null)) {
			throw new IllegalStateException("can't receive null sample weights");
		}
		try {
			return OBJECT_MAPPER.writeValueAsString(list);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Can't convert list to json string");
		}
	}

	@Override
	public List<OrdinalAmount<BigDecimal>> convertToEntityAttribute(String jsonMap) {
		if(jsonMap == null) {
			return null;
		}
        try {
            return OBJECT_MAPPER.readValue(jsonMap, new TypeReference<List<OrdinalAmount<BigDecimal>>>() { });
        } catch (IOException e) {
        	throw new IllegalStateException("Can't convert json string to list");
        }
	}

}
