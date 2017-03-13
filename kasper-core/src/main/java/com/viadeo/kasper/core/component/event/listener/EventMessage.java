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
package com.viadeo.kasper.core.component.event.listener;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.KasperMessage;
import org.axonframework.domain.DomainEventMessage;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Decorator for Axon Event messages
 *
 * @param <E> Encapsulated Kasper event class
 * 
 * @see EventMessage
 * @see com.viadeo.kasper.api.component.event.Event
 */
public class EventMessage<E extends Event> extends KasperMessage<E> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventMessage.class);

    private final Optional<KasperID> optionalKapserID;
    private final DateTime timestamp;

    // ------------------------------------------------------------------------

	public EventMessage(final org.axonframework.domain.EventMessage<E> eventMessage) {
		this(
                getEntityId(eventMessage),
                eventMessage.getTimestamp(),
                MoreObjects.firstNonNull(
                        (Context) eventMessage.getMetaData().get(Context.METANAME),
                        Contexts.empty()
                ),
                eventMessage.getPayload()
        );
	}

    public EventMessage(Optional<KasperID> entityId, DateTime timestamp, Context context, E event) {
        super(context, event);
        this.optionalKapserID = entityId;
        this.timestamp = timestamp;
    }

    // ------------------------------------------------------------------------


    public Optional<KasperID> getEntityId() {
        return optionalKapserID;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public E getEvent() {
		return getInput();
	}

    public Long getVersion() {
        return getContext().getVersion();
    }

    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("event", getEvent())
                .add("context", getContext())
                .add("version", getVersion())
                .toString();
    }

    // ------------------------------------------------------------------------

    protected static Optional<KasperID> getEntityId(final org.axonframework.domain.EventMessage<? extends Event> eventMessage) {
        if (eventMessage instanceof DomainEventMessage) {
            Object identifier = ((DomainEventMessage) eventMessage).getAggregateIdentifier();
            if (identifier != null && identifier instanceof KasperID) {
                return Optional.of((KasperID) identifier);
            } else {
                LOGGER.warn(
                        "A domain event message has no identifier or the expected type of identifier, <identifier={}> <eventType={}>",
                        identifier, eventMessage.getPayloadType()
                );
            }
        }
        return Optional.absent();
    }
}
