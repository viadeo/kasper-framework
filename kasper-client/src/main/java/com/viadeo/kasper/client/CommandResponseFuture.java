// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client;

import com.sun.jersey.api.client.ClientResponse;
import com.viadeo.kasper.api.domain.command.CommandResponse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Preconditions.checkNotNull;

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
        return kasperClient.handleCommandResponse(futureResponse().get());
    }

    public CommandResponse get(final long timeout, final TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return kasperClient.handleCommandResponse(futureResponse().get(timeout, unit));
    }

}
