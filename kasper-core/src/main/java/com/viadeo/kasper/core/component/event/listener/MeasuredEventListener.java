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

public class MeasuredEventListener
        extends MeasuredHandler<Event, EventMessage<Event>, EventResponse, EventListener<Event>>
        implements EventListener<Event>
{

    public MeasuredEventListener(MetricRegistry metricRegistry, EventListener<Event> handler) {
        super(metricRegistry, handler, EventListener.class);
    }

    @Override
    public void handle(org.axonframework.domain.EventMessage event) {
        handle(new EventMessage<Event>(event));
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
