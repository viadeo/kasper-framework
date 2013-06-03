// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandResult;
import com.viadeo.kasper.cqrs.command.impl.KasperErrorCommandResult;

public class ObjectMapperProvider {
	public static final ObjectMapperProvider instance = new ObjectMapperProvider();

	private final ObjectWriter writer;
	private final ObjectReader reader;
	private final ObjectMapper mapper;

    // ------------------------------------------------------------------------

	private ObjectMapperProvider() {
		mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, true);
		mapper.configure(MapperFeature.AUTO_DETECT_CREATORS, true);
		mapper.configure(MapperFeature.AUTO_DETECT_FIELDS, true);
		mapper.configure(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, true);
		mapper.configure(MapperFeature.USE_ANNOTATIONS, true);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		final Module kasperClientModule = new SimpleModule()
				.addDeserializer(KasperCommandResult.class,
						new KasperCommandResultDeserializer())
				.addDeserializer(KasperErrorCommandResult.class,
						new KasperErrorCommandResultDeserializer())
				.addSerializer(new KasperErrorCommandResultSerializer())
				.addSerializer(new KasperCommandResultSerializer());

		mapper.registerModule(kasperClientModule).registerModule(
				new GuavaModule());

		writer = mapper.writer();
		reader = mapper.reader();
	}

	/**
	 * @return the configured instance of ObjectWriter to use. This writer is
	 *         shared between server and client code thus do not reconfigure it.
	 */
	public ObjectWriter objectWriter() {
		return writer;
	}

	/**
	 * @return the configured instance of ObjectReader to use. This reader is
	 *         shared between server and client code thus do not reconfigure it.
	 */
	public ObjectReader objectReader() {
		return reader;
	}

	/**
	 * @return this instance should not be modified.
	 */
	public ObjectMapper mapper() {
		return mapper;
	}

}
