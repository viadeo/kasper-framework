// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client;

import com.sun.jersey.api.client.PartialRequestBuilder;
import com.viadeo.kasper.context.Context;

import static com.viadeo.kasper.context.HttpContextHeaders.*;

public class HttpContextSerializer {

    private static final void setHeader(final PartialRequestBuilder builder, final String header, final Object value) {
        if ((null != value) && ! value.toString().isEmpty()) {
            builder.header(header, value.toString());
        }
    }

    public void serialize(final Context context, final PartialRequestBuilder builder) {

            setHeader(builder, HEADER_SESSION_CORRELATION_ID, context.getSessionCorrelationId());
            setHeader(builder, HEADER_FUNNEL_CORRELATION_ID, context.getFunnelCorrelationId());
            setHeader(builder, HEADER_REQUEST_CORRELATION_ID, context.getRequestCorrelationId());
            setHeader(builder, HEADER_USER_ID, context.getUserId());
            setHeader(builder, HEADER_USER_LANG, context.getUserLang());
            setHeader(builder, HEADER_USER_COUNTRY, context.getUserCountry());
            setHeader(builder, HEADER_APPLICATION_ID, context.getApplicationId());
            setHeader(builder, HEADER_SECURITY_TOKEN, context.getSecurityToken());
            setHeader(builder, HEADER_FUNNEL_NAME, context.getFunnelName());
            setHeader(builder, HEADER_FUNNEL_VERSION, context.getFunnelVersion());
    }

}
