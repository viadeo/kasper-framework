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

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.api.response.KasperResponse;
import com.viadeo.kasper.core.component.MeasuredHandler;
import com.viadeo.kasper.core.metrics.MetricNames;

import java.util.Set;

public class MeasuredEventListener<EVENT extends Event>
        extends MeasuredHandler<EVENT, EventMessage<EVENT>, EventResponse, EventListener<EVENT>>
        implements EventListener<EVENT>
{

    public MeasuredEventListener(MetricRegistry metricRegistry, EventListener<EVENT> handler) {
        super(metricRegistry, handler, EventListener.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handle(org.axonframework.domain.EventMessage event) {
        handle(new EventMessage<EVENT>(event));
    }

    @Override
    public String getName() {
        return handler.getName();
    }

    @Override
    public Set<EventDescriptor> getEventDescriptors() {
        return handler.getEventDescriptors();
    }

    @Override
    public EventResponse error(KasperReason reason) {
        return EventResponse.failure(reason);
    }

    @Override
    protected boolean isErrorResponse(final KasperResponse response) {
        switch (response.getStatus()) {
            case OK:
            case SUCCESS:
            case ACCEPTED:
            case REFUSED:
            case ERROR:
                return Boolean.FALSE;

            case FAILURE:
            default :
                return Boolean.TRUE;
        }
    }

    @Override
    protected MetricNames instantiateGlobalMetricNames(Class<?> componentClass) {
        return MetricNames.of(componentClass, "errors", "handle-time");
    }

    @Override
    protected MetricNames instantiateInputMetricNames() {
        return MetricNames.of(handler.getHandlerClass(), "errors", "handle-time");
    }

    @Override
    protected MetricNames instantiateDomainMetricNames() {
        return MetricNames.byDomainOf(handler.getHandlerClass(), "errors", "handle-time");
    }

}
