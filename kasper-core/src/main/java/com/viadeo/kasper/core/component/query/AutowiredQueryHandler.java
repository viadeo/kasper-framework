// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.query;

import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.exception.KasperQueryException;
import com.viadeo.kasper.core.component.query.gateway.QueryGateway;
import com.viadeo.kasper.core.context.CurrentContext;
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
public abstract class AutowiredQueryHandler<Q extends Query, RESULT extends QueryResult>
    extends BaseQueryHandler<Q,RESULT>
    implements WirableQueryHandler<Q,RESULT>
{

    private transient EventBus eventBus;
    private transient QueryGateway queryGateway;

    // ------------------------------------------------------------------------

    protected AutowiredQueryHandler() { }

    // ------------------------------------------------------------------------

    /**
     * @param context The context
     * @param query The command to handle
     * @return the command response
     */
    @Override
    public QueryResponse<RESULT> handle(Context context, Q query) throws Exception {
        try {
            return retrieve(query);
        } catch (final UnsupportedOperationException e) {
            try {
                return retrieve(new QueryMessage<>(context, query));
            } catch (final UnsupportedOperationException e1) {
                throw new UnsupportedOperationException();
            }
        }
    }

    public QueryResponse<RESULT> retrieve(final QueryMessage<Q> message) throws Exception {
        throw new UnsupportedOperationException();
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

    @Override
    public void setEventBus(final EventBus eventBus) {
        this.eventBus = checkNotNull(eventBus);
    }

    @Override
    public void setQueryGateway(final QueryGateway queryGateway) {
        this.queryGateway = checkNotNull(queryGateway);
    }

}

