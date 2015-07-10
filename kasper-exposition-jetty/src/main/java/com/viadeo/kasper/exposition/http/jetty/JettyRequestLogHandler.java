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

public class JettyRequestLogHandler extends HandlerWrapper {

    public static final Logger LOGGER = LoggerFactory.getLogger(JettyRequestLogHandler.class);

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String correlationId = Objects.firstNonNull(baseRequest.getHeader("X-KASPER-REQUEST-CID"), "-");
        String path = baseRequest.getRequestURI();
        long timestamp = baseRequest.getTimeStamp();
        DateTime time = new DateTime(timestamp);

        String message = String.format("%s %s %s", time.toString(), path, correlationId);

        LOGGER.debug("BEFORE HANDLE: {}", message);
        long tic = System.currentTimeMillis();

        try {
            super.handle(target, baseRequest, request, response);
        } finally {
            long handleDuration = System.currentTimeMillis() - tic;
            LOGGER.debug("AFTER HANDLE ({} ms): {}", handleDuration, message);
        }
    }
}
