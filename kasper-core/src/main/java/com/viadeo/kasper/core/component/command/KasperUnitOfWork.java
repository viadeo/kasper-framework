// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.exception.KasperCommandException;
import com.viadeo.kasper.core.metrics.MetricNameStyle;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.unitofwork.DefaultUnitOfWork;
import org.axonframework.unitofwork.TransactionManager;
import org.joda.time.DateTime;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.getMetricRegistry;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

/**
 * The Kasper unit of work
 */
public class KasperUnitOfWork extends DefaultUnitOfWork {

    private final Map<EventBus, List<EventMessage<?>>> eventsToBePublished = new HashMap<>();

    private static final String GLOBAL_METER_EVENTS_NAME = name(KasperUnitOfWork.class, "committed");

    // ------------------------------------------------------------------------

    public KasperUnitOfWork() {
        super();
    }

    public KasperUnitOfWork(final TransactionManager<?> transactionManager) {
        super(checkNotNull(transactionManager));
    }

    public static KasperUnitOfWork startAndGet() {
        final KasperUnitOfWork uow = new KasperUnitOfWork();
        uow.start();
        return uow;
    }

    public static KasperUnitOfWork startAndGet(final TransactionManager<?> transactionManager) {
        final KasperUnitOfWork uow = new KasperUnitOfWork(checkNotNull(transactionManager));
        uow.start();
        return uow;
    }

    // ------------------------------------------------------------------------

    /**
     * Intercept and record events
     */
    @Override
    public void registerForPublication(final EventMessage<?> message, final EventBus eventBus, final boolean notifyRegistrationHandlers) {

        super.registerForPublication(
                checkNotNull(message),
                checkNotNull(eventBus),
                notifyRegistrationHandlers
        );

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

        final boolean notifyRegistrationHandlers = false;

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

                this.registerForPublication(uowMessage, entry.getKey(), notifyRegistrationHandlers);

            }
        }

        super.publishEvents();
    }

    @Override
    protected void notifyListenersAfterCommit() {

        super.notifyListenersAfterCommit();

        /*
         * Publish metrics
         */
        int nbCommittedEvents = 0;
        while ( ! eventsToBePublished.isEmpty()) {
            final Iterator<Map.Entry<EventBus, List<EventMessage<?>>>> iterator = eventsToBePublished.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry<EventBus, List<EventMessage<?>>> entry = iterator.next();
                final List<EventMessage<?>> messageList = entry.getValue();

                for (final EventMessage message : messageList) {
                    final Event event = (Event) message.getPayload();
                    nbCommittedEvents++;

                    getMetricRegistry().meter(
                        name(event.getClass(), "committed")
                    ).mark();

                    getMetricRegistry().meter(
                        name(MetricNameStyle.DOMAIN_TYPE, event.getClass(), "committed")
                    ).mark();
                }

                iterator.remove();
            }
        }

        getMetricRegistry().meter(GLOBAL_METER_EVENTS_NAME).mark(nbCommittedEvents);
    }

}
