// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandResult;

import java.io.IOException;

public class KasperCommandResultSerializer extends StdSerializer<KasperCommandResult> {

	public KasperCommandResultSerializer() {
		super(KasperCommandResult.class);
	}

	@Override
	public void serialize(final KasperCommandResult result, final JsonGenerator jp, final SerializerProvider provider)
            throws IOException {
		jp.writeStartObject();
		jp.writeObjectField("status", result.getStatus());
		jp.writeEndObject();
	}

}
