// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.http;

import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.cqrs.query.QueryAnswer;
import com.viadeo.kasper.cqrs.query.QueryResponse;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.Response.Status;

public class HTTPQueryResponse<ANSWER extends QueryAnswer>  extends QueryResponse<ANSWER> {

    private final Status httpStatus;

    // ------------------------------------------------------------------------

    public HTTPQueryResponse(final Status httpStatus, final QueryResponse<ANSWER> result) {
        super(result);
        this.httpStatus = checkNotNull(httpStatus);
    }

    public HTTPQueryResponse(final ANSWER answer) {
        super(checkNotNull(answer));
        this.httpStatus = Status.OK;
    }

    public HTTPQueryResponse(final Status httpStatus, final ANSWER answer) {
        super(checkNotNull(answer));
        this.httpStatus = checkNotNull(httpStatus);
    }

    public HTTPQueryResponse(final Status httpStatus, final KasperError error) {
        super(checkNotNull(error));
        this.httpStatus = checkNotNull(httpStatus);
    }

    // ------------------------------------------------------------------------

    public Status getHTTPStatus() {
        return this.httpStatus;
    }

}
