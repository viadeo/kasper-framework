// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.Tags;
import com.viadeo.kasper.context.impl.AbstractContext;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.impl.DefaultKasperId;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.context.HttpContextHeaders.*;

/**
 * Extract context from an HTTP request
 */
public class HttpContextDeserializer {

    public Context deserialize(final HttpServletRequest req, final UUID kasperCorrelationId) {
        checkNotNull(req);
        checkNotNull(kasperCorrelationId);

        // --------------------------------------------------------------------

        final String headerSessionCorrelationId = req.getHeader(HEADER_SESSION_CORRELATION_ID);
        final String headerFunnelCorrelationId = req.getHeader(HEADER_FUNNEL_CORRELATION_ID);
        final String headerRequestCorrelationId = req.getHeader(HEADER_REQUEST_CORRELATION_ID);
        final String headerUserId = req.getHeader(HEADER_USER_ID);
        final String headerUserLang = req.getHeader(HEADER_USER_LANG);
        final String headerUserCountry = req.getHeader(HEADER_USER_COUNTRY);
        final String headerApplicationId = req.getHeader(HEADER_APPLICATION_ID);
        final String headerSecurityToken = req.getHeader(HEADER_SECURITY_TOKEN);
        final String headerFunnelName = req.getHeader(HEADER_FUNNEL_NAME);
        final String headerFunnelVersion = req.getHeader(HEADER_FUNNEL_VERSION);
        final String headerIpAddress = req.getHeader(HEADER_REQUEST_IP_ADDRESS);
        final String headerTags = req.getHeader(HEADER_TAGS);
        final String headerCallType = req.getHeader(HEADER_CALL_TYPE);
        final String headerUserAgent = req.getHeader(HEADER_USER_AGENT);
        final String headerReferer = req.getHeader(HEADER_REFERER);

        // --------------------------------------------------------------------

        final Context context = DefaultContextBuilder.get();

        if (AbstractContext.class.isAssignableFrom(context.getClass())) {
            ((AbstractContext) context).setKasperCorrelationId(new DefaultKasperId(kasperCorrelationId));
        }

        if (null != headerSessionCorrelationId) {
            context.setSessionCorrelationId(headerSessionCorrelationId);
        }

        if (null != headerFunnelCorrelationId) {
            context.setFunnelCorrelationId(headerFunnelCorrelationId);
        }

        if (null != headerRequestCorrelationId) {
            context.setRequestCorrelationId(headerRequestCorrelationId);
        }

        if (null != headerUserId) {
            context.setUserId(headerUserId);
        }

        if (null != headerUserLang) {
            context.setUserLang(headerUserLang);
        }

        if (null != headerUserCountry) {
            context.setUserCountry(headerUserCountry);
        }

        if (null != headerApplicationId) {
            context.setApplicationId(headerApplicationId);
        }

        if (null != headerSecurityToken) {
            context.setSecurityToken(headerSecurityToken);
        }

        if (null != headerFunnelName) {
            context.setFunnelName(headerFunnelName);
        }

        if (null != headerFunnelVersion) {
            context.setFunnelVersion(headerFunnelVersion);
        }

        if (null != headerIpAddress) {
            context.setIpAddress(headerIpAddress);
        }

        if (null != headerTags) {
            context.setTags(Tags.valueOf(headerTags));
        }

        if (null != headerCallType) {
            context.setProperty(Context.CALL_TYPE, headerCallType);
        }

        if (null != headerUserAgent) {
            context.setProperty(Context.USER_AGENT, headerUserAgent);
        }

        if (null != headerReferer) {
            context.setProperty(Context.REFERER, headerReferer);
        }

        return context;
    }

}
