// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client;

import com.google.common.base.Optional;
import com.sun.jersey.api.client.RequestBuilder;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.Tags;

import java.io.Serializable;

import static com.viadeo.kasper.context.HttpContextHeaders.*;

public class HttpContextSerializer {

    private static void setHeader(final RequestBuilder builder, final String header, final Object value) {
        if ((null != value) && (! value.toString().isEmpty())) {
            builder.header(header, value.toString());
        }
    }

    public void serialize(final Context context, final RequestBuilder builder) {
        setHeader(builder, HEADER_SESSION_CORRELATION_ID, context.getSessionCorrelationId());
        setHeader(builder, HEADER_FUNNEL_CORRELATION_ID, context.getFunnelCorrelationId());
        setHeader(builder, HEADER_REQUEST_CORRELATION_ID, context.getRequestCorrelationId());
        setHeader(builder, HEADER_USER_ID, context.getUserId());
        setHeader(builder, HEADER_USER_LANG, context.getUserLang());
        setHeader(builder, HEADER_USER_COUNTRY, context.getUserCountry());
        setHeader(builder, HEADER_APPLICATION_ID, context.getApplicationId());
        setHeader(builder, HEADER_SECURITY_TOKEN, context.getSecurityToken());
        setHeader(builder, HEADER_ACCESS_TOKEN, context.getAccessToken());
        setHeader(builder, HEADER_FUNNEL_NAME, context.getFunnelName());
        setHeader(builder, HEADER_FUNNEL_VERSION, context.getFunnelVersion());
        setHeader(builder, HEADER_REQUEST_IP_ADDRESS, context.getIpAddress());
        setHeader(builder, HEADER_TAGS, Tags.toString(context.getTags()));

        final Optional<Serializable> callType = context.getProperty(Context.CALL_TYPE);
        if (callType.isPresent()) {
            setHeader(builder, HEADER_CALL_TYPE, callType.get());
        }

        final Optional<Serializable> userAgent = context.getProperty(Context.USER_AGENT);
        if (userAgent.isPresent()) {
            setHeader(builder, HEADER_USER_AGENT, userAgent.get());
        }

        final Optional<Serializable> referer = context.getProperty(Context.REFERER);
        if (referer.isPresent()) {
            setHeader(builder, HEADER_REFERER, referer.get());
        }
    }

}
