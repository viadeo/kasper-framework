// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.platform.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.AbstractContext;
import com.viadeo.kasper.core.boot.AnnotationRootProcessor;
import com.viadeo.kasper.core.boot.ComponentsInstanceManager;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.platform.Platform;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.EventBus;

import java.util.Map;

/**
 * The default implementation for the Kasper platform
 */
public class KasperPlatform implements Platform {

    /** The platform components **/
    protected CommandGateway commandGateway;
    protected QueryGateway queryGateway;
    protected AnnotationRootProcessor rootProcessor;
    protected EventBus eventBus;

    private volatile Boolean _booted = false;
    private static final Integer SYNC = 42;

    // ------------------------------------------------------------------------

    @Override
    public void boot() {
        synchronized (SYNC) {
            if (!_booted) {
                this.rootProcessor.boot();
                _booted = true;
            }
        }
    }

    @Override
    public boolean isBooted() {
        return _booted;
    }

    // ------------------------------------------------------------------------

    @Override
    public void setCommandGateway(final CommandGateway commandGateway) {
        this.commandGateway = Preconditions.checkNotNull(commandGateway);
    }

    @Override
    public CommandGateway getCommandGateway() {
        return this.commandGateway;
    }

    @Override
    public void sendCommand(final Command command, final Context context) throws Exception {
        this.commandGateway.sendCommand(command, context);
    }

    // ------------------------------------------------------------------------

    @Override
    public AnnotationRootProcessor getRootProcessor() {
        return this.rootProcessor;
    }

    @Override
    public void setRootProcessor(final AnnotationRootProcessor rootProcessor) {
        this.rootProcessor = Preconditions.checkNotNull(rootProcessor);
    }

    @Override
    public ComponentsInstanceManager getComponentsInstanceManager() {
        return this.rootProcessor.getComponentsInstanceManager();
    }

    // ------------------------------------------------------------------------

    @Override
    public void setQueryGateway(final QueryGateway queryGateway) {
        this.queryGateway = Preconditions.checkNotNull(queryGateway);
    }

    @Override
    public QueryGateway getQueryGateway() {
        return this.queryGateway;
    }

    @Override
    public <RES extends QueryResult> RES retrieve(final Query query, final Context context) throws Exception {
        return this.queryGateway.retrieve(query, context);
    }

    // ------------------------------------------------------------------------

    @Override
    public void setEventBus(final EventBus eventBus) {
        this.eventBus = Preconditions.checkNotNull(eventBus);
    }

    @Override
    public EventBus getEventBus() {
        return this.eventBus;
    }

    @Override
    public void publishEvent(final Event event) {
        Preconditions.checkNotNull(event);
        Preconditions.checkState(event.getContext().isPresent(), "Context must be present !");

        final Context context = event.getContext().get();

        /* Sets a valid Kasper correlation id if required */
        if (AbstractContext.class.isAssignableFrom(context.getClass())) {
            final AbstractContext kasperContext = (AbstractContext) context;
            kasperContext.setValidKasperCorrelationId();
        }

        final Map<String, Object> metaData = Maps.newHashMap();
        metaData.put(Context.METANAME, Preconditions.checkNotNull(context));

        final GenericEventMessage<Event> eventMessageAxon =
                new GenericEventMessage<>(event, metaData);

        this.eventBus.publish(eventMessageAxon);
    }

    @Override
    public void publishEvent(final Event event, final Context context) {
        Preconditions.checkNotNull(event).setContext(context);
        this.publishEvent(event);
    }

}
