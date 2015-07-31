// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.core.component.MeasuredHandler;
import com.viadeo.kasper.core.component.command.gateway.CommandGateway;

public class MeasuredCommandHandler
        extends MeasuredHandler<CommandResponse, Command, CommandHandler<Command>>
        implements CommandHandler<Command>
{

    public MeasuredCommandHandler(
            final MetricRegistry metricRegistry,
            final CommandHandler commandHandler
    ) {
        super(metricRegistry, commandHandler, CommandGateway.class);
    }

    @Override
    public CommandResponse error(KasperReason reason) {
        return CommandResponse.error(reason);
    }
}
