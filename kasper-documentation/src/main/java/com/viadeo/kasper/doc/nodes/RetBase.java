// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Charsets;

public abstract class RetBase implements Serializable {
	private static final long serialVersionUID = 1864387214923151069L;
	
	private static ObjectMapper mapper = new ObjectMapper();
	static {
        mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, true);
        mapper.configure(MapperFeature.AUTO_DETECT_CREATORS, true);
        mapper.configure(MapperFeature.AUTO_DETECT_FIELDS, true);
        mapper.configure(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, true);
        mapper.configure(MapperFeature.USE_ANNOTATIONS, true);
        //mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        //mapper.setVisibility(PropertyAccessor.CREATOR, Visibility.ANY);
	}
	
	private final String type;
	
	// ------------------------------------------------------------------------
	
	protected RetBase(final String type) {
		this.type = type;
	}

	// ------------------------------------------------------------------------
	
	public String getType() {
		return type;
	}
	
	public String toJson() {
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		try {
			mapper.writeValue(os, this);
		} catch (final JsonGenerationException e) {
			e.printStackTrace();
			return "";
		} catch (final JsonMappingException e) {
			e.printStackTrace();
			return "";
		} catch (final IOException e) {
			e.printStackTrace();
			return "";
		}
		
		try {
			return os.toString(Charsets.UTF_8.name());
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}
	
}
