// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.google.common.base.Optional;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.Contexts;
import com.viadeo.kasper.context.HttpContextHeaders;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Extract context from an HTTP request
 */
public class HttpContextDeserializer {

    public Context deserialize(final HttpServletRequest req, final UUID kasperCorrelationId) {
        checkNotNull(req);
        checkNotNull(kasperCorrelationId);

        Context.Builder builder = Contexts.builder(kasperCorrelationId);

        for (String headerName : Collections.list(req.getHeaderNames())) {
            Optional<HttpContextHeaders> httpContextHeader = HttpContextHeaders.fromHeader(headerName);
            if (httpContextHeader.isPresent()) {
                builder = builder.with(httpContextHeader.get().toPropertyKey(), req.getHeader(headerName));
            } else {
                builder = builder.with(headerName, req.getHeader(headerName));
            }
        }

        return builder.build();
    }

}
