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
package com.viadeo.kasper.core.component.event.saga;

import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.core.component.event.listener.AxonEventListener;
import com.viadeo.kasper.core.component.event.listener.EventDescriptor;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.core.component.event.listener.EventMessage;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class SagaWrapper extends AxonEventListener<Event> implements EventListener<Event> {

    private final SagaExecutor<Saga> executor;

    // ------------------------------------------------------------------------

    public SagaWrapper(final SagaExecutor<Saga> executor) {
        super();
        this.executor = checkNotNull(executor);
    }

    // ------------------------------------------------------------------------

    @Override
    public EventResponse handle(final EventMessage<Event> message) {
        try {
            executor.execute(message.getContext(), message.getInput());
        } catch (final Exception e) {
            return EventResponse.error(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, e));
        }
        return EventResponse.success();
    }

    @Override
    public void rollback(final EventMessage<Event> message) {
        // nothing
    }

    @Override
    public String getName() {
        return executor.getSagaClass().getName();
    }

    @Override
    public Set<EventDescriptor> getEventDescriptors() {
        return executor.getEventClasses();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Class<Event> getInputClass() {
        return Event.class;
    }

    @Override
    public Class<?> getHandlerClass() {
        return executor.getSagaClass();
    }
}
