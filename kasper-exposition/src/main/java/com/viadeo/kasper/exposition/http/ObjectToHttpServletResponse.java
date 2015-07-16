// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.viadeo.kasper.api.response.KasperResponse;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class ObjectToHttpServletResponse {

    protected final ObjectWriter writer;

    // ------------------------------------------------------------------------

    protected ObjectToHttpServletResponse(final ObjectMapper objectMapper) {
        this.writer = objectMapper.writer();
    }

    // ------------------------------------------------------------------------

    public HttpServletResponse map(
            final HttpServletResponse httpResponse,
            final KasperResponse response,
            final Response.Status status
    ) throws IOException {
        try (final JsonGenerator generator = writer.getFactory().createGenerator(httpResponse.getOutputStream())) {
            httpResponse.setStatus(status.getStatusCode());
            writer.writeValue(generator, response);
        }
        return httpResponse;
    }

}
