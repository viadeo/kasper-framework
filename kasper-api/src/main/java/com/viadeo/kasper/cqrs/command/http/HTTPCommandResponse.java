// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.http;

import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.cqrs.command.CommandResponse;

import javax.ws.rs.core.Response;

public class HTTPCommandResponse extends CommandResponse {

    private final Response.Status httpStatus;

    // ------------------------------------------------------------------------

    public HTTPCommandResponse(final Response.Status httpStatus, final CommandResponse response) {
        super(response);
        this.httpStatus = httpStatus;
    }

    public HTTPCommandResponse(final CommandResponse.Status status, KasperReason error) {
        super(status, error);
        this.httpStatus = Response.Status.OK;
    }

    public HTTPCommandResponse(final Response.Status httpStatus, final CommandResponse.Status status, KasperReason error) {
        super(status, error);
        this.httpStatus = httpStatus;
    }

    // ------------------------------------------------------------------------

    public Response.Status getHTTPStatus() {
        return this.httpStatus;
    }

}
