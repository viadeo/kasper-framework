// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.http;

import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.cqrs.command.CommandResult;

import javax.ws.rs.core.Response;

public class HTTPCommandResult extends CommandResult {

    private final Response.Status httpStatus;

    // ------------------------------------------------------------------------

    public HTTPCommandResult(final Response.Status httpStatus, final CommandResult result) {
        super(result);
        this.httpStatus = httpStatus;
    }

    public HTTPCommandResult(final CommandResult.Status status, KasperError error) {
        super(status, error);
        this.httpStatus = Response.Status.OK;
    }

    public HTTPCommandResult(final Response.Status httpStatus, final CommandResult.Status status, KasperError error) {
        super(status, error);
        this.httpStatus = httpStatus;
    }

    // ------------------------------------------------------------------------

    public Response.Status getHTTPStatus() {
        return this.httpStatus;
    }

}
