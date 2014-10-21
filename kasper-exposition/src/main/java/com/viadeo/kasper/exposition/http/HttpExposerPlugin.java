// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viadeo.kasper.client.platform.plugin.Plugin;

import javax.servlet.http.HttpServlet;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public abstract class HttpExposerPlugin<EXPOSER extends HttpServlet> implements Plugin {

    private final HttpContextDeserializer contextDeserializer;
    private final ObjectMapper mapper;

    private EXPOSER exposer;

    // ------------------------------------------------------------------------

    public HttpExposerPlugin(
            final HttpContextDeserializer contextDeserializer,
            final ObjectMapper mapper
    ) {

        this.contextDeserializer = checkNotNull(contextDeserializer);
        this.mapper = checkNotNull(mapper);
    }

    // ------------------------------------------------------------------------

    protected void initialize(final EXPOSER exposer) {
        this.exposer = checkNotNull(exposer);
    }

    public EXPOSER getHttpExposer() {
        checkState(exposer != null, "The plugin should be initialized.");
        return exposer;
    }

    protected ObjectMapper getMapper() {
        return mapper;
    }

    protected HttpContextDeserializer getContextDeserializer() {
        return contextDeserializer;
    }

}
