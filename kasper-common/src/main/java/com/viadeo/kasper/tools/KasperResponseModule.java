// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.viadeo.kasper.api.domain.response.KasperReason;
import com.viadeo.kasper.api.domain.command.CommandResponse;
import com.viadeo.kasper.api.domain.query.QueryResponse;

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
