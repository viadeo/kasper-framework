// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client;

import com.sun.jersey.api.client.ClientResponse;
import com.viadeo.kasper.api.component.command.CommandResponse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;

class CommandResponseFuture extends ResponseFuture<CommandResponse> {

    private KasperClient kasperClient;

    // ------------------------------------------------------------------------

    public CommandResponseFuture(final KasperClient kasperClient,
                                 final Future<ClientResponse> futureResponse) {
        super(futureResponse);
        this.kasperClient = checkNotNull(kasperClient);
    }

    // ------------------------------------------------------------------------

    public CommandResponse get() throws InterruptedException, ExecutionException {
        try {
            return get(KasperClient.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw propagate(e);
        }
    }

    public CommandResponse get(final long timeout, final TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        ClientResponse clientResponse = futureResponse().get(timeout, unit);
        try {
            return kasperClient.handleCommandResponse(clientResponse);
        } finally {
            kasperClient.closeClientResponse(clientResponse);
        }
    }

}
