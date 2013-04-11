package com.viadeo.platform.tests;

import java.io.IOException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import junit.framework.TestCase;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.viadeo.kasper.IKasperID;
import com.viadeo.kasper.context.IContext;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.event.domain.er.impl.AbstractConceptEvent;

public class JacksonSerializationTest extends TestCase {

	static private String entity_uuid = "99900000-1234-8619-9e27-8745bced513f";
	static private String modificationDate = "2013-02-15";
	static private String value = "value";

	// ------------------------------------------------------------------------

	@SuppressWarnings("serial")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class TestEvent extends AbstractConceptEvent {

		final private String test;
		final private String[] values;

		protected TestEvent() {
			test = "test";
			values = null;
		}

		public TestEvent(final IKasperID id_shortMessage, final DateTime creationDate) {
			super(id_shortMessage, creationDate);
			this.test = "test";
			values = new String[0];
		}

	}

	// ------------------------------------------------------------------------

	private static final String json = "" 
			+ "{" 
			+ "   \"test\" : \"new\","
			+ "	  \"id\" : \"" + entity_uuid + "\"," 
			+ "	  \"lastEntityModificationDate\" : \"" + modificationDate + "\"," 
			+ "	  \"values\" : \"" + value + "\""
			+ "}";

	// ========================================================================

	class JodaDateTimeInstanciator extends ValueInstantiator {
		
		@Override
		public String getValueTypeDesc() {
			return DateTime.class.getName();
		}

		@Override
		public boolean canCreateFromString() {
			return true;
		}

		@Override
		public Object createFromString(DeserializationContext ctxt, String value)
				throws IOException, JsonProcessingException {
			return new DateTime(value);
		}
	}
	
	// ------------------------------------------------------------------------

	public void testDeserialize() throws JsonParseException,
			JsonMappingException, IOException {

		// Given
		final ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(MapperFeature.AUTO_DETECT_CREATORS, true);
		mapper.configure(MapperFeature.AUTO_DETECT_FIELDS, true);
		mapper.configure(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, true);
		mapper.configure(MapperFeature.USE_ANNOTATIONS, false);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		mapper.setVisibility(PropertyAccessor.CREATOR, Visibility.ANY);

		final SimpleModule module = new SimpleModule("test", Version.unknownVersion());
		module.addAbstractTypeMapping(IContext.class, DefaultContextBuilder.DefaultContext.class);
		module.addAbstractTypeMapping(IKasperID.class, DefaultContextBuilder.DefaultKasperId.class);
		module.addValueInstantiator(DateTime.class, new JodaDateTimeInstanciator());
		mapper.registerModule(module);

		// When
		final TestEvent event = mapper.readValue(json, TestEvent.class);

		// Then
		assertTrue(event.getEntityId().equals(new DefaultContextBuilder.DefaultKasperId(entity_uuid)));
		assertEquals(new DateTime(modificationDate), event.getEntityLastModificationDate());
		assertEquals(event.values[0], value);
		assertEquals(event.test, "new");
	}

}
