// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.AbstractContext;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.impl.StringKasperId;

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

        UUID sessionCorrelationId = null;
        UUID funnelCorrelationId = null;
        UUID requestCorrelationId = null;
        KasperID userId = null;
        String userLang = null;
        String userCountry = null;
        String securityToken = null;
        String applicationId = null;

        // --------------------------------------------------------------------

        final String headerSessionCorrelationId = req.getHeader(HEADER_SESSION_CORRELATION_ID);
        if (null != headerSessionCorrelationId) {
            sessionCorrelationId = UUID.fromString(headerSessionCorrelationId);
        }

        final String headerFunnelCorrelationId = req.getHeader(HEADER_FUNNEL_CORRELATION_ID);
        if (null != headerFunnelCorrelationId) {
            funnelCorrelationId = UUID.fromString(headerFunnelCorrelationId);
        }

        final String headerRequestCorrelationId = req.getHeader(HEADER_REQUEST_CORRELATION_ID);
        if (null != headerRequestCorrelationId) {
            requestCorrelationId = UUID.fromString(headerRequestCorrelationId);
        }

        final String headerUserId = req.getHeader(HEADER_USER_ID);
        if (null != headerUserId) {
            userId = new StringKasperId(headerUserId);
        }

        final String headerUserLang = req.getHeader(HEADER_USER_LANG);
        if (null != headerUserLang) {
            userLang = headerUserLang;
        }

        final String headerUserCountry = req.getHeader(HEADER_USER_COUNTRY);
        if (null != headerUserCountry) {
            userCountry = headerUserCountry;
        }

        final String headerApplicationId = req.getHeader(HEADER_APPLICATION_ID);
        if (null != headerApplicationId) {
            applicationId = headerApplicationId;
        }

        final String headerSecurityToken = req.getHeader(HEADER_SECURITY_TOKEN);
        if (null != headerSecurityToken) {
            securityToken = headerSecurityToken;
        }

        // --------------------------------------------------------------------

        final Context context = DefaultContextBuilder.get();

        if (AbstractContext.class.isAssignableFrom(context.getClass())) {
            ((AbstractContext) context).setKasperCorrelationId(new DefaultKasperId(kasperCorrelationId));
        }

        if (null != sessionCorrelationId) {
            context.setSessionCorrelationId(new DefaultKasperId(sessionCorrelationId));
        }

        if (null != funnelCorrelationId) {
            context.setFunnelCorrelationId(new DefaultKasperId(funnelCorrelationId));
        }

        if (null != requestCorrelationId) {
            context.setRequestCorrelationId(new DefaultKasperId(requestCorrelationId));
        }

        if (null != userId) {
            context.setUserId(userId);
        }

        if (null != userLang) {
            context.setUserLang(userLang);
        }

        if (null != userCountry) {
            context.setUserCountry(userCountry);
        }

        if (null != applicationId) {
            context.setApplicationId(applicationId);
        }

        if (null != securityToken) {
            context.setSecurityToken(securityToken);
        }

        return context;
    }

}
