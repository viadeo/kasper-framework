// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client;

import com.google.common.base.Optional;
import com.sun.jersey.api.client.RequestBuilder;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.common.context.HttpContextHeaders;

import java.util.Map;

public class HttpContextSerializer {


    private static void setHeader(final RequestBuilder builder, final String header, final Object value) {
        if ((null != value) && (! value.toString().isEmpty())) {
            builder.header(header, value.toString());
        }
    }

    public void serialize(final Context context, final RequestBuilder builder) {
        for (final Map.Entry<String, String> entry : context.asMap().entrySet()) {
            Optional<HttpContextHeaders> httpContextHeader = HttpContextHeaders.fromPropertyKey(entry.getKey());
            if (httpContextHeader.isPresent()) {
                setHeader(builder, httpContextHeader.get().toString(), entry.getValue());
            } else {
                setHeader(builder, entry.getKey(), entry.getValue());
            }
        }
    }

}
