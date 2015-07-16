// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.common.serde;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.viadeo.kasper.api.id.ID;
import com.viadeo.kasper.api.id.IDBuilder;
import com.viadeo.kasper.api.id.RelationID;

import java.io.IOException;

public class IDModule extends SimpleModule {

    public IDModule(final IDBuilder builder) {
        addSerializer(ID.class, new IDSerializer());
        addKeySerializer(ID.class, new IDKeySerializer());
        addDeserializer(ID.class, new IDDeserializer(builder));
        addKeyDeserializer(ID.class, new IDKeyDeserializer(builder));

        addSerializer(RelationID.class, new RelationIDSerializer());
        addDeserializer(RelationID.class, new RelationIDDeserializer(builder));
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

    private static class IDKeySerializer extends JsonSerializer<ID> {

        @Override
        public void serialize(final ID value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
            jgen.writeFieldName(value.toString());
        }
    }

    private static class IDKeyDeserializer extends KeyDeserializer {
        private IDBuilder builder;

        public IDKeyDeserializer(IDBuilder builder) {
            this.builder = builder;
        }

        @Override
        public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
            return builder.build(key);
        }
    }

    private static class RelationIDSerializer extends JsonSerializer<RelationID> {
        @Override
        public void serialize(final RelationID value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
            jgen.writeString(value.toString());
        }
    }

    private static class RelationIDDeserializer extends JsonDeserializer<RelationID> {

        private final IDBuilder builder;

        public RelationIDDeserializer(IDBuilder builder) {
            this.builder = builder;
        }

        @Override
        public RelationID deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
            String[] split = jp.getText().split("---");
            return new RelationID(builder.build(split[0]), builder.build(split[1]));
        }
    }
}
