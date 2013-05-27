package com.viadeo.kasper.tools;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandResult;

public class KasperCommandResultSerializer extends
		StdSerializer<KasperCommandResult> {
	public KasperCommandResultSerializer() {
		super(KasperCommandResult.class);
	}

	@Override
	public void serialize(KasperCommandResult result, JsonGenerator jp,
			SerializerProvider provider) throws IOException,
			JsonGenerationException {
		jp.writeStartObject();
		jp.writeObjectField("status", result.getStatus());
		jp.writeEndObject();
	}
}
