// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.cqrs.command.exceptions.KasperCommandException;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.impl.UnitOfWorkEvent;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.unitofwork.DefaultUnitOfWork;
import org.axonframework.unitofwork.TransactionManager;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The Kasper unit of work
 */
public class KasperUnitOfWork extends DefaultUnitOfWork {

    private final Map<EventBus, List<EventMessage<?>>> eventsToBePublished = new HashMap<EventBus, List<EventMessage<?>>>();

    // ------------------------------------------------------------------------

    public KasperUnitOfWork() {
        super();
    }

    public KasperUnitOfWork(final TransactionManager<?> transactionManager) {
        super(transactionManager);
    }

    public static KasperUnitOfWork startAndGet() {
        final KasperUnitOfWork uow = new KasperUnitOfWork();
        uow.start();
        return uow;
    }

    public static KasperUnitOfWork startAndGet(TransactionManager<?> transactionManager) {
        final KasperUnitOfWork uow = new KasperUnitOfWork(transactionManager);
        uow.start();
        return uow;
    }

    // ------------------------------------------------------------------------

    /**
     * Intercept and record events
     */
    @Override
    public void registerForPublication(final EventMessage<?> message, final EventBus eventBus) {
        super.registerForPublication(checkNotNull(message), checkNotNull(eventBus));

        if ( ! UnitOfWorkEvent.class.isAssignableFrom(message.getPayloadType())) {
            final List<EventMessage<?>> events;
            if (eventsToBePublished.containsKey(eventBus)) {
                events = eventsToBePublished.get(eventBus);
            } else {
                events = Lists.newArrayList();
                eventsToBePublished.put(eventBus, events);
            }
            events.add(message);
        }
    }

    /**
     * Publish a macro
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void publishEvents() {

        for (final Map.Entry<EventBus, List<EventMessage<?>>> entry : eventsToBePublished.entrySet()) {
            if (entry.getValue().size() > 1) {
                final List<String> events = Lists.newArrayList();

                final String uowEventId = UUID.randomUUID().toString();
                Optional<Context> context = Optional.absent();
                for (final EventMessage<?> message : entry.getValue()) {
                    final Event event = (Event) message.getPayload();
                    events.add(message.getIdentifier());

                    /* Each event should have a context at this step, but ensure it */
                    if ( ! context.isPresent() && message.getMetaData().containsKey(Context.METANAME)) {
                        context = Optional.of((Context) message.getMetaData().get(Context.METANAME));
                    }

                    event.setUOWEventId(uowEventId);
                }

                /* Should not occur.. */
                if ( ! context.isPresent()) {
                    throw new KasperCommandException("Unable to determine a valid context for the UnitOfWorkEvent");
                }

                final UnitOfWorkEvent uowEvent = new UnitOfWorkEvent(events);
                final GenericEventMessage uowMessage = new GenericEventMessage(
                        uowEventId,
                        DateTime.now(),
                        uowEvent,
                        context.get().asMetaDataMap()
                );

                this.registerForPublication(uowMessage, entry.getKey());

            } else {

                final EventMessage message = (EventMessage) entry.getValue().get(0);
                final Event event = (Event) entry.getValue().get(0).getPayload();

                /* this uniq event is itself the uowEvent */
                event.setUOWEventId(message.getIdentifier());
            }
        }

        super.publishEvents();
    }

}
