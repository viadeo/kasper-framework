// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.query;

import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.core.component.query.gateway.QueryGateway;
import org.axonframework.eventhandling.EventBus;

/**
 * A class implements this interface in order to have the capability to be auto wired with the platform components.
 *
 * @param <QUERY> the handled query class
 * @param <RESULT> the returned result
 *
 * @see com.viadeo.kasper.core.component.command.CommandHandler
 */
public interface WirableQueryHandler<QUERY extends Query, RESULT extends QueryResult>
        extends QueryHandler<QUERY,RESULT>
{

    /**
     * Wires an event bus on this <code>QueryHandler</code> instance.
     * @param eventBus an event bus
     */
    void setEventBus(EventBus eventBus);

    /**
     * Wires a query gateway on this <code>QueryHandler</code> instance.
     * @param queryGateway a query gateway
     */
    void setQueryGateway(QueryGateway queryGateway);

}
