// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.base.Optional;
import com.viadeo.kasper.cqrs.command.impl.KasperErrorCommandResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

// WTF? Genson can handle deserialization using custom and 0 annotation, jackson die! :)
public class KasperErrorCommandResultDeserializer extends StdDeserializer<KasperErrorCommandResult> {
	
	private static final long serialVersionUID = -3933078791498408546L;
	private static final Logger LOGGER = LoggerFactory.getLogger(KasperCommandResultDeserializer.class);
	private static final String MESSAGE = "errorMessage";

    // ------------------------------------------------------------------------

	public KasperErrorCommandResultDeserializer() {
		super(KasperErrorCommandResult.class);
	}

    // ------------------------------------------------------------------------

	@Override
	public KasperErrorCommandResult deserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException {

		Optional<String> errorMessage = Optional.absent();

		while (!jp.nextToken().equals(JsonToken.END_OBJECT)) {
			final String name = jp.getCurrentName();
			jp.nextToken();

			if (MESSAGE.equals(name)) {
				errorMessage = jp.readValueAs(new TypeReference<Optional<String>>() { });
			} else {
				LOGGER.warn("Unknown property when default mapping DTO");
				// FIXME do we just ignore unknown properties or take some action?
			}
		}

		// TODO
		return new KasperErrorCommandResult(errorMessage.orNull());
	}

}
