package com.viadeo.kasper.client.platform.impl;

import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.query.QueryGateway;

public class DefaultPlatform implements Platform {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private final KasperEventBus eventBus;

    public DefaultPlatform(CommandGateway commandGateway, QueryGateway queryGateway, KasperEventBus eventBus) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
        this.eventBus = eventBus;
    }

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
