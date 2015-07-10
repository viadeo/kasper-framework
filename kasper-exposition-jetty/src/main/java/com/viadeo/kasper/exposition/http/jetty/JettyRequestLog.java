package com.viadeo.kasper.exposition.http.jetty;

import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;

import java.io.IOException;

public class JettyRequestLog extends NCSARequestLog {


    protected void logExtended(Request request,
                               Response response,
                               StringBuilder b) throws IOException
    {
        super.logExtended(request, response, b);
        String correlationId = request.getHeader("X-KASPER-REQUEST-CID");
        if (correlationId == null)
            b.append("\"-\" ");
        else
        {
            b.append('"');
            b.append(correlationId);
            b.append("\" ");
        }
    }
}
