// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client;

import com.google.common.reflect.TypeToken;
import com.sun.jersey.api.client.ClientResponse;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;

class QueryResponseFuture<P extends QueryResult> extends ResponseFuture<QueryResponse<P>> {
    private final TypeToken<P> mapTo;
    private KasperClient kasperClient;

    // ------------------------------------------------------------------------

    public QueryResponseFuture(final KasperClient kasperClient,
                               final Future<ClientResponse> futureResponse,
                               final TypeToken<P> mapTo) {
        super(futureResponse);

        this.kasperClient = checkNotNull(kasperClient);
        this.mapTo = checkNotNull(mapTo);
    }

    // ------------------------------------------------------------------------

    public QueryResponse<P> get() throws InterruptedException, ExecutionException {
        try {
            return get(KasperClient.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            futureResponse().cancel(true);
            throw propagate(e);
        }
    }

    public QueryResponse<P> get(final long timeout, final TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        ClientResponse clientResponse = futureResponse().get(timeout, unit);
        try {
            return kasperClient.handleQueryResponse(clientResponse, mapTo);
        } finally {
            kasperClient.closeClientResponse(clientResponse);
        }
    }

}
