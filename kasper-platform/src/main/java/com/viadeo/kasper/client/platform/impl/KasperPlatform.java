// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.impl;

import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.query.QueryGateway;

import static com.google.common.base.Preconditions.checkNotNull;

public class KasperPlatform implements Platform {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private final KasperEventBus eventBus;

    // ------------------------------------------------------------------------

    public KasperPlatform(final CommandGateway commandGateway,
                          final QueryGateway queryGateway,
                          final KasperEventBus eventBus) {
        this.commandGateway = checkNotNull(commandGateway);
        this.queryGateway = checkNotNull(queryGateway);
        this.eventBus = checkNotNull(eventBus);
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

}
