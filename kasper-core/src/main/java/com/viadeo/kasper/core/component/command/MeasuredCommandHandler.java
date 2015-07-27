// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.core.metrics.MetricNameStyle;
import com.viadeo.kasper.core.metrics.MetricNames;
import org.axonframework.repository.ConflictingAggregateVersionException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

/**
 * This implementation of <code>CommandHandler</code> allows to add metrics.
 * 
 * @param <COMMAND> the command class handled by this <code>CommandHandler</code>.
 */
public class MeasuredCommandHandler<COMMAND extends Command> implements CommandHandler<COMMAND> {

    private final CommandHandler<COMMAND> commandHandler;
    private final MetricRegistry metricRegistry;

    private MetricNames commandMetricNames;
    private MetricNames domainMetricNames;

    public MeasuredCommandHandler(
            final MetricRegistry metricRegistry,
            final CommandHandler<COMMAND> commandHandler
    ) {
        this.metricRegistry = checkNotNull(metricRegistry);
        this.commandHandler = checkNotNull(commandHandler);
    }

    protected MetricNames getOrInstantiateCommandMetricNames() {
        final Class<COMMAND> commandClass = commandHandler.getCommandClass();

        if (commandMetricNames == null) {
            commandMetricNames = MetricNames.of(commandClass);
        }

        return commandMetricNames;
    }

    protected MetricNames getOrInstantiateDomainMetricNames() {
        final Class<COMMAND> commandClass = commandHandler.getCommandClass();

        if (domainMetricNames == null) {
            domainMetricNames = MetricNames.byDomainOf(commandClass);
        }

        return domainMetricNames;
    }

    @Override
    public CommandResponse handle(final Context context, final COMMAND command) throws Exception {
        final MetricNames commandMetricNames = getOrInstantiateCommandMetricNames();
        final MetricNames domainMetricNames = getOrInstantiateDomainMetricNames();

        metricRegistry.meter(commandMetricNames.requests).mark();
        metricRegistry.meter(domainMetricNames.requests).mark();
        metricRegistry.meter(name(MetricNameStyle.CLIENT_TYPE, context, getCommandClass(), "requests")).mark();

        final Timer.Context commandTimer = metricRegistry.timer(commandMetricNames.requestsTime).time();
        final Timer.Context domainTimer = metricRegistry.timer(domainMetricNames.requestsTime).time();

        CommandResponse response;

        try {
            response = commandHandler.handle(context, command);
        } catch (final ConflictingAggregateVersionException e) {
            response = CommandResponse.error(CoreReasonCode.CONFLICT, e.getMessage());
        } catch (final RuntimeException e) {
            response = CommandResponse.error(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, e));
        } finally {
            commandTimer.stop();
            domainTimer.stop();
        }

        switch (response.getStatus()) {
            case OK:
            case SUCCESS:
            case ACCEPTED:
            case REFUSED:
                // nothing
                break;

            case ERROR:
            case FAILURE:
                metricRegistry.meter(commandMetricNames.errors).mark();
                metricRegistry.meter(domainMetricNames.errors).mark();
                metricRegistry.meter(name(MetricNameStyle.CLIENT_TYPE, context, getCommandClass(), "errors")).mark();
                break;
        }

        return response;
    }

    @Override
    public Class<COMMAND> getCommandClass() {
        return commandHandler.getCommandClass();
    }

    @Override
    public Class<? extends CommandHandler> getCommandHandlerClass() {
        return commandHandler.getCommandHandlerClass();
    }

}
