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
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.query.gateway.QueryGateway;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 *
 * This is a convenient class
 *
 * Extend it instead of implementing IQueryHandler will allow you to
 * override at your convenience handle(query)
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

    private transient KasperEventBus eventBus;
    private transient QueryGateway queryGateway;

    // ------------------------------------------------------------------------

    protected AutowiredQueryHandler() { }

    // ------------------------------------------------------------------------

    /**
     * @param context The context
     * @param query The command to handle
     * @return the command response
     */
    public QueryResponse<RESULT> handle(Context context, Q query) {
        try {
            return handle(query);
        } catch (final UnsupportedOperationException e) {
            throw new UnsupportedOperationException();
        }
    }


    public QueryResponse<RESULT> handle(final Q query) {
        throw new UnsupportedOperationException();
    }

    // ------------------------------------------------------------------------

    /**
     * Publish an event on the event bus
     *
     * @param event The event
     */
    public void publish(final Context context, final Event event) {
        checkNotNull(event, "The specified event must be non null");
        checkNotNull(context, "The specified context must be non null");
        checkState(eventBus != null, "Unable to publish the specified event : the event bus is null");

        this.eventBus.publish(context, event);
    }

    // ------------------------------------------------------------------------

    public QueryGateway getQueryGateway() {
        return queryGateway;
    }

    // ------------------------------------------------------------------------

    @Override
    public void setEventBus(final KasperEventBus eventBus) {
        this.eventBus = checkNotNull(eventBus);
    }

    @Override
    public void setQueryGateway(final QueryGateway queryGateway) {
        this.queryGateway = checkNotNull(queryGateway);
    }

}

