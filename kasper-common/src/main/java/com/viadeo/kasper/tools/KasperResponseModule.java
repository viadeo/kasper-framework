// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.query.QueryResponse;

public class KasperResponseModule extends SimpleModule {

    public KasperResponseModule() {
        super();
        setDeserializers(new CommandQueryResponseDeserializerAdapter());
        addSerializer(QueryResponse.class, new QueryResponseSerializer());
        addSerializer(CommandResponse.class, new CommandResponseSerializer());
        addDeserializer(CommandResponse.class, new CommandResponseDeserializer());
        addSerializer(CommandResponse.class, new CommandResponseSerializer());
        addDeserializer(CommandResponse.class, new CommandResponseDeserializer());
        addDeserializer(KasperReason.class, new KasperReasonDeserializer());
    }
}
