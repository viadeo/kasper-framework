// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client;

import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;

import javax.ws.rs.core.Response;

import static com.google.common.base.Preconditions.checkNotNull;

public class HTTPQueryResponse<RESULT extends QueryResult> extends QueryResponse<RESULT> implements HTTPKasperResponse {

    private final Response.Status httpStatus;

    // ------------------------------------------------------------------------

    public HTTPQueryResponse(final Response.Status httpStatus, final QueryResponse<RESULT> response) {
        super(response);
        this.httpStatus = checkNotNull(httpStatus);
    }

    public HTTPQueryResponse(final RESULT result) {
        super(checkNotNull(result));
        this.httpStatus = Response.Status.OK;
    }

    public HTTPQueryResponse(final Response.Status httpStatus, final RESULT result) {
        super(checkNotNull(result));
        this.httpStatus = checkNotNull(httpStatus);
    }

    public HTTPQueryResponse(final Response.Status httpStatus, final KasperReason reason) {
        super(checkNotNull(reason));
        this.httpStatus = checkNotNull(httpStatus);
    }

    // ------------------------------------------------------------------------

    public Response.Status getHTTPStatus() {
        return this.httpStatus;
    }

}
