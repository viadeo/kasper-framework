// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

import com.viadeo.kasper.api.domain.query.Query;
import com.viadeo.kasper.api.domain.query.QueryResult;
import com.viadeo.kasper.api.domain.query.QueryResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.context.CurrentContext;
import com.viadeo.kasper.api.domain.exception.KasperQueryException;
import com.viadeo.kasper.api.domain.event.Event;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.EventBus;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

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
    public static final int PARAMETER_QUERY_POSITION = 0;

    /**
     * Generic parameter position for Data Transfer Object
     */
    public static final int PARAMETER_RESULT_POSITION = 1;

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
    public void publish(final Event event) {
        checkNotNull(event, "The specified event must be non null");
        checkState(eventBus != null, "Unable to publish the specified event : the event bus is null");
        final EventMessage eventMessage = GenericEventMessage.asEventMessage(event);
        this.eventBus.publish(eventMessage);
    }

    // ------------------------------------------------------------------------

    public QueryGateway getQueryGateway() {
        return queryGateway;
    }

    public Context getContext() {
        if (CurrentContext.value().isPresent()) {
            return CurrentContext.value().get();
        }
        throw new KasperQueryException("Unexpected condition : no context was set during query handling");
    }

    // ------------------------------------------------------------------------

    public void setEventBus(final EventBus eventBus) {
        this.eventBus = checkNotNull(eventBus);
    }

    public void setQueryGateway(final QueryGateway queryGateway) {
        this.queryGateway = checkNotNull(queryGateway);
    }

}

