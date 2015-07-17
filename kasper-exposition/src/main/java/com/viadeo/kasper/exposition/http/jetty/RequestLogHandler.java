// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http.jetty;

import com.google.common.base.Objects;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RequestLogHandler extends HandlerWrapper {

    public static final Logger LOGGER = LoggerFactory.getLogger(RequestLogHandler.class);

    @Override
    public void handle(
            final String target,
            final Request baseRequest,
            final HttpServletRequest request,
            final HttpServletResponse response) throws IOException, ServletException {

        final String correlationId = Objects.firstNonNull(baseRequest.getHeader("X-KASPER-REQUEST-CID"), "-");
        final String path = baseRequest.getRequestURI();
        final long timestamp = baseRequest.getTimeStamp();
        final DateTime time = new DateTime(timestamp);
        final String message = String.format("%s %s %s", time.toString(), path, correlationId);

        LOGGER.debug("BEFORE HANDLE: {}", message);
        final long tic = System.currentTimeMillis();

        try {
            super.handle(target, baseRequest, request, response);
        } finally {
            final long handleDuration = System.currentTimeMillis() - tic;
            LOGGER.debug("AFTER HANDLE ({} ms): {}", handleDuration, message);
        }

    }

}
