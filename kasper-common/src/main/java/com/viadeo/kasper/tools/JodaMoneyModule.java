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
import org.joda.money.Money;

import java.io.IOException;

import static com.fasterxml.jackson.core.JsonToken.VALUE_STRING;

public class JodaMoneyModule extends SimpleModule {
    private static final String NAME = "JodaMoneyModule";

    // ------------------------------------------------------------------------

    public JodaMoneyModule() {
        super(NAME);

        this.addSerializer(Money.class, new MoneyJsonSerializer());
        this.addDeserializer(Money.class, new MoneyJsonDeserializer());
    }

    // ------------------------------------------------------------------------

    private static class MoneyJsonSerializer extends JsonSerializer<Money> {
        @Override
        public void serialize(final Money value, final JsonGenerator jgen, final SerializerProvider provider)
                throws IOException {
            jgen.writeString(value.toString());
        }
    }

    private static class MoneyJsonDeserializer extends JsonDeserializer<Money> {
        @Override
        public Money deserialize(final JsonParser jp, final DeserializationContext ctxt)
                throws IOException {
            if (VALUE_STRING.equals(jp.getCurrentToken())) {
                return Money.parse(jp.getText());
            }
            throw ctxt.mappingException("Can only convert JSON String (ISO4217) to Joda Money object.");
        }
    }

}
