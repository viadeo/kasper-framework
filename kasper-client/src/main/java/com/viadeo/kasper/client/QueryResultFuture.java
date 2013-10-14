// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client;

import com.google.common.reflect.TypeToken;
import com.sun.jersey.api.client.ClientResponse;
import com.viadeo.kasper.cqrs.query.QueryAnswer;
import com.viadeo.kasper.cqrs.query.QueryResult;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class QueryResultFuture<P extends QueryAnswer> extends ResultFuture<QueryResult<P>> {

    private final TypeToken<P> mapTo;
    private KasperClient kasperClient;

    // ------------------------------------------------------------------------

    public QueryResultFuture(final KasperClient kasperClient, final Future<ClientResponse> futureResponse, final TypeToken<P> mapTo) {
        super(futureResponse);

        this.kasperClient = kasperClient;
        this.mapTo = mapTo;
    }

    // ------------------------------------------------------------------------

    public QueryResult<P> get() throws InterruptedException, ExecutionException {
        return kasperClient.handleQueryResponse(futureResponse().get(), mapTo);
    }

    public QueryResult<P> get(final long timeout, final TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return kasperClient.handleQueryResponse(futureResponse().get(timeout, unit), mapTo);
    }

}
