// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform;

import com.viadeo.kasper.core.component.command.gateway.CommandGateway;
import com.viadeo.kasper.core.component.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.query.gateway.QueryGateway;

import static com.google.common.base.Preconditions.checkNotNull;

public class KasperPlatform implements Platform {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private final KasperEventBus eventBus;
    private final Meta meta;

    // ------------------------------------------------------------------------

    public KasperPlatform(final CommandGateway commandGateway,
                          final QueryGateway queryGateway,
                          final KasperEventBus eventBus,
                          final Meta meta
    ) {
        this.commandGateway = checkNotNull(commandGateway);
        this.queryGateway = checkNotNull(queryGateway);
        this.eventBus = checkNotNull(eventBus);
        this.meta = checkNotNull(meta);
    }

    // ------------------------------------------------------------------------

    @Override
    public CommandGateway getCommandGateway() {
        return commandGateway;
    }

    @Override
    public QueryGateway getQueryGateway() {
        return queryGateway;
    }

    @Override
    public KasperEventBus getEventBus() {
        return eventBus;
    }

    @Override
    public Meta getMeta() {
        return meta;
    }

}
