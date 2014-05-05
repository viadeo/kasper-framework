// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContext;
import org.axonframework.domain.MetaData;

import java.io.IOException;
import java.util.Map;

public class MetaDataDeserializer extends JsonDeserializer<MetaData> {

    @SuppressWarnings("unchecked")
    @Override
    public MetaData deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        final Map map = jp.readValueAs(Map.class);
        final Object object = map.get(Context.METANAME);

        if (null != object) {
            final DefaultContext defaultContext = new DefaultContext((Map<String, String>) object);
            map.put(Context.METANAME, defaultContext);
        }

        return new MetaData(map);
    }
}