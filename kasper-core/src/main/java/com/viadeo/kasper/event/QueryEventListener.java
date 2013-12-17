// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event;

import com.google.common.base.Optional;
import com.viadeo.kasper.cqrs.query.QueryGateway;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class QueryEventListener<E extends IEvent> extends EventListener<E> {

    private QueryGateway queryGateway;

    public void setQueryGateway(final QueryGateway queryGateway) {
        this.queryGateway = checkNotNull(queryGateway);
    }

    protected Optional<QueryGateway> getQueryGateway() {
        return Optional.fromNullable(this.queryGateway);
    }

}
