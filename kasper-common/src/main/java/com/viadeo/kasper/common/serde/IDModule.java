// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.common.serde;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.viadeo.kasper.api.ID;
import com.viadeo.kasper.api.IDBuilder;

import java.io.IOException;

public class IDModule extends SimpleModule {

    public IDModule(final IDBuilder builder) {
        addSerializer(ID.class, new IDSerializer());
        addDeserializer(ID.class, new IDDeserializer(builder));
    }

    private static class IDSerializer extends JsonSerializer<ID> {

        @Override
        public void serialize(final ID value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
            jgen.writeString(value.toString());
        }
    }

    private static class IDDeserializer extends JsonDeserializer<ID> {

        private IDBuilder builder;

        public IDDeserializer(IDBuilder builder) {

            this.builder = builder;
        }

        @Override
        public ID deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
            return builder.build(jp.getText());
        }
    }
}
