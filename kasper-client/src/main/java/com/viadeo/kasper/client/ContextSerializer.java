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

public class ContextSerializer {

    public void serialize(final Context context, final PartialRequestBuilder builder) {

        if (null != context.getSessionCorrelationId()) {
            builder.header(HEADER_SESSION_CORRELATION_ID, context.getSessionCorrelationId());
        }

        if (null != context.getFunnelCorrelationId()) {
            builder.header(HEADER_FUNNEL_CORRELATION_ID, context.getFunnelCorrelationId());
        }

        if (null != context.getRequestCorrelationId()) {
            builder.header(HEADER_REQUEST_CORRELATION_ID, context.getRequestCorrelationId());
        }

        if (null != context.getUserId()) {
            builder.header(HEADER_USER_ID, context.getUserId());
        }

        if (null != context.getUserLang()) {
            builder.header(HEADER_USER_LANG, context.getUserLang());
        }

        if (null != context.getUserCountry()) {
            builder.header(HEADER_USER_COUNTRY, context.getUserCountry());
        }

        if (null != context.getApplicationId()) {
            builder.header(HEADER_APPLICATION_ID, context.getApplicationId());
        }

        if (null != context.getSecurityToken()) {
            builder.header(HEADER_SECURITY_TOKEN, context.getSecurityToken());
        }

    }

}
