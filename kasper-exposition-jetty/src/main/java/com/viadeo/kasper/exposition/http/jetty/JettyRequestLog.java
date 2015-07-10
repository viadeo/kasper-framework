// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http.jetty;

import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;

import java.io.IOException;

public class JettyRequestLog extends NCSARequestLog {


    protected void logExtended(final Request request,
                               final Response response,
                               final StringBuilder b) throws IOException
    {
        super.logExtended(request, response, b);
        final String correlationId = request.getHeader("X-KASPER-REQUEST-CID");
        if (correlationId == null) {
            b.append("\"-\" ");
        } else {
            b.append('"');
            b.append(correlationId);
            b.append("\" ");
        }
    }

}
