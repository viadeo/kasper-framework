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
import org.axonframework.unitofwork.UnitOfWork;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void registerForPublication(final EventMessage<?> event, final EventBus eventBus) {
        super.registerForPublication(checkNotNull(event), checkNotNull(eventBus));

        final List<EventMessage<?>> events;
        if (eventsToBePublished.containsKey(eventBus)) {
            events = eventsToBePublished.get(eventBus);
        } else {
            events = Lists.newArrayList();
            eventsToBePublished.put(eventBus, events);
        }
        events.add(event);
    }

    /**
     * Publish a macro
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void publishEvents() {

        for (final Map.Entry<EventBus, List<EventMessage<?>>> entry : eventsToBePublished.entrySet()) {
            if (entry.getValue().size() > 1) {
                final List<Event> events = Lists.newArrayList();

                Optional<Context> context = Optional.absent();
                for (final EventMessage<?> message : entry.getValue()) {
                    final Event event = (Event) message.getPayload();
                    events.add(event);

                    /* Each event should have a context at this step, but ensure it */
                    if ( ! context.isPresent() && event.getContext().isPresent()) {
                        context = Optional.of(event.getContext().get().child());
                    }
                }

                /* Should not occur.. */
                if ( ! context.isPresent()) {
                    throw new KasperCommandException("Unable to determine a valid context for the UnitOfWorkEvent");
                }

                final UnitOfWorkEvent uowEvent = new UnitOfWorkEvent(events);
                uowEvent.setContext(context.get());
                super.registerForPublication(new GenericEventMessage(uowEvent), entry.getKey());

            } else {
                final Event event = (Event) entry.getValue().get(0).getPayload();

                /* this uniq event is itself the uowEvent */
                event.setUOWEventId(event.getId());
            }
        }

        super.publishEvents();
    }

}
