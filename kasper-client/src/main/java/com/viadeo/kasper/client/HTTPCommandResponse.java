// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client;

import com.viadeo.kasper.api.domain.response.KasperReason;
import com.viadeo.kasper.api.domain.command.CommandResponse;

import javax.ws.rs.core.Response;

import static com.google.common.base.Preconditions.checkNotNull;

public class HTTPCommandResponse extends CommandResponse implements HTTPKasperResponse {

    private final Response.Status httpStatus;

    // ------------------------------------------------------------------------

    public HTTPCommandResponse(final Response.Status httpStatus, final CommandResponse response) {
        super(response);
        this.httpStatus = checkNotNull(httpStatus);
    }

    public HTTPCommandResponse(final CommandResponse.Status status, KasperReason reason) {
        super(status, reason);
        this.httpStatus = Response.Status.OK;
    }

    public HTTPCommandResponse(final Response.Status httpStatus, final CommandResponse.Status status, KasperReason reason) {
        super(status, reason);
        this.httpStatus = checkNotNull(httpStatus);
    }

    // ------------------------------------------------------------------------

    public Response.Status getHTTPStatus() {
        return this.httpStatus;
    }

}
