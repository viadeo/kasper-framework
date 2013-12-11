// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.event.IEvent;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.EventBus;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * This is a convenient class
 *
 * Extend it instead of implementing IQueryHandler will allow you to
 * override at your convenience retrieve(message) or simply retrieve(query)
 * if you are not interested by the message
 *
 * The query gateway is aware of this internal convenience and will deal with it
 *
 * @param <Q> the query
 * @param <RESULT> the Response
 */
public abstract class QueryHandler<Q extends Query, RESULT extends QueryResult> {

    /**
     * Generic parameter position for Data Query Object
     */
    public static int PARAMETER_QUERY_POSITION = 0;

    /**
     * Generic parameter position for Data Transfer Object
     */
    public static int PARAMETER_RESULT_POSITION = 1;

    private transient EventBus eventBus;
    private transient QueryGateway queryGateway;

    // ------------------------------------------------------------------------

    protected QueryHandler() { }

    // ------------------------------------------------------------------------

    public QueryResponse<RESULT> retrieve(final QueryMessage<Q> message) throws Exception {
        return retrieve(message.getQuery());
    }

    public QueryResponse<RESULT> retrieve(final Q query) throws Exception {
        throw new UnsupportedOperationException();
    }

    // ------------------------------------------------------------------------

    /**
     * Publish an event on the event bus
     *
     * @param event The event
     */
    public void publish(final IEvent event) {
        checkNotNull(event, "The specified event must be non null");
        Preconditions.checkState(eventBus != null, "Unable to publish the specified event : the event bus is null");
        final EventMessage eventMessage = GenericEventMessage.asEventMessage(event);
        this.eventBus.publish(eventMessage);
    }

    // ------------------------------------------------------------------------

    public QueryGateway getQueryGateway() {
        return queryGateway;
    }

    // ------------------------------------------------------------------------

    public void setEventBus(final EventBus eventBus) {
        this.eventBus = checkNotNull(eventBus);
    }

    public void setQueryGateway(final QueryGateway queryGateway) {
        this.queryGateway = checkNotNull(queryGateway);
    }

}

