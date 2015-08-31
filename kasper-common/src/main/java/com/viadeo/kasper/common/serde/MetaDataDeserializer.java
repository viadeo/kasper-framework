package com.viadeo.kasper.common.serde;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Preconditions;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.ContextHelper;
import org.axonframework.domain.MetaData;

import java.io.IOException;
import java.util.Map;

public class MetaDataDeserializer extends JsonDeserializer<MetaData> {

    private final ContextHelper contextHelper;

    public MetaDataDeserializer(final ContextHelper contextHelper) {
        this.contextHelper = Preconditions.checkNotNull(contextHelper);
    }

    @SuppressWarnings("unchecked")
    @Override
    public MetaData deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        final Map map = jp.readValueAs(Map.class);
        final Object object = map.get(Context.METANAME);

        if (null != object) {
            final Context defaultContext = contextHelper.createFrom((Map<String, String>) object);
            map.put(Context.METANAME, defaultContext);
        }

        return new MetaData(map);
    }
}
