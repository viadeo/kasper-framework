// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.viadeo.kasper.exception.KasperException;

import java.io.IOException;
import java.lang.reflect.Method;

import static com.google.common.base.Preconditions.checkNotNull;

public class LowerEnumModule extends SimpleModule {

    private static final long serialVersionUID = -3897575151769316482L;

    // ------------------------------------------------------------------------

    @Override
    public void setupModule(final SetupContext context) {
        super.setupModule(checkNotNull(context));
        final Deserializers.Base deser = new Deserializers.Base() {
            @SuppressWarnings("unchecked")
            @Override
            public JsonDeserializer<?> findEnumDeserializer(
                    final Class<?> type,
                    final DeserializationConfig config,
                    final BeanDescription beanDesc)
                    throws JsonMappingException {
                final Class<Enum<?>> enumClass = (Class<Enum<?>>) type;
                return new LowerEnumDeserializer(enumClass);
            }
        };
        context.addDeserializers(deser);
    }

    // ------------------------------------------------------------------------

    public static class LowerEnumDeserializer extends StdScalarDeserializer<Enum<?>> {

        private static final long serialVersionUID = -8106277798060764736L;

        protected LowerEnumDeserializer(final Class<Enum<?>> clazz) {
            super(clazz);
        }

        @Override
        public Enum<?> deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
            final String text = jp.getText().toUpperCase();
            final Class<?> enumClass = handledType();

            try {
                final Method valueOfMethod = enumClass.getDeclaredMethod("valueOf", String.class);
                return (Enum<?>) valueOfMethod.invoke(null, text);
            } catch (final Exception e) {
                throw new KasperException("Cannot deserialize enum " + enumClass.getName() + " from '" + text + "'", e);
            }
        }
    }

}
