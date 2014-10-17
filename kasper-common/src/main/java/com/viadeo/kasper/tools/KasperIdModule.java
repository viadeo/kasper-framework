// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.impl.DefaultKasperRelationId;

import java.io.IOException;

public class KasperIdModule extends SimpleModule {

    public KasperIdModule() {
        super();
        setDeserializers(new KasperIdAdapter());
        addDeserializer(DefaultKasperId.class, new DefaultKasperIdDeserializer());
        addDeserializer(DefaultKasperRelationId.class, new DefaultKasperRelationIdDeserializer());
        addSerializer(DefaultKasperId.class, new DefaultKasperIdSerializer());
        addSerializer(DefaultKasperRelationId.class, new DefaultKasperRelationIdSerializer());
    }

    public static class DefaultKasperRelationIdSerializer extends JsonSerializer<DefaultKasperRelationId> {
        @Override
        public void serialize(
                final DefaultKasperRelationId value,
                final JsonGenerator jsongenerator,
                final SerializerProvider provider
        ) throws IOException {
            jsongenerator.writeString(value.toString());
        }
    }

    public static final class DefaultKasperRelationIdDeserializer extends JsonDeserializer<DefaultKasperRelationId> {
        @Override
        public DefaultKasperRelationId deserialize(
                final JsonParser jp,
                final DeserializationContext context
        ) throws IOException {
            final String textValue = jp.getText();
            return new DefaultKasperRelationId(textValue);
        }
    }

    public static class DefaultKasperIdSerializer extends JsonSerializer<DefaultKasperId> {
        @Override
        public void serialize(
                final DefaultKasperId value,
                final JsonGenerator jsongenerator,
                final SerializerProvider provider
        ) throws IOException {
            jsongenerator.writeString(value.toString());
        }
    }

    public static final class DefaultKasperIdDeserializer extends JsonDeserializer<DefaultKasperId> {
        @Override
        public DefaultKasperId deserialize(final JsonParser jp, final DeserializationContext context) throws IOException {
            final String textValue = jp.getText();
            return new DefaultKasperId(textValue);
        }
    }
}
