package com.viadeo.kasper.tools;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.query.QueryResponse;

public class KasperResponseModule extends SimpleModule {

    public KasperResponseModule() {
        super();
        setDeserializers(new CommandQueryResponseDeserializerAdapter());
        addSerializer(CommandResponse.class, new CommandResponseSerializer());
        addSerializer(QueryResponse.class, new QueryResponseSerializer());
        addDeserializer(CommandResponse.class, new CommandResponseDeserializer());
    }
}
