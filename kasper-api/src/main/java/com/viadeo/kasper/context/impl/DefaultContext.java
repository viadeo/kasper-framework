// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.context.impl;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.context.Context;

/**
 *
 * A default {@link com.viadeo.kasper.context.Context} implementation
 * @see com.viadeo.kasper.context.Context
 *
 */
public class DefaultContext extends AbstractContext {
    private static final long serialVersionUID = -2357451589032314740L;

    private KasperID userId;
    private String userLang;

    private KasperID requestCorrelationId;
    private KasperID sessionCorrelationId;

    // ------------------------------------------------------------------------

    public DefaultContext() {
        super();

        this.userId = DEFAULT_USER_ID;
        this.userLang = DefaultContext.DEFAULT_USER_LANG;
        this.requestCorrelationId = DEFAULT_REQCORR_ID;
        this.sessionCorrelationId = DEFAULT_SESSCORR_ID;
    }

    @Override
    public KasperID getUserId() {
        return this.userId;
    }

    @Override
    public DefaultContext setUserId(final KasperID userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public String getUserLang() {
        return this.userLang;
    }

    @Override
    public void setUserLang(final String userLang) {
        this.userLang = userLang;
    }

    @Override
    public void setRequestCorrelationId(KasperID requestCorrelationId) {
        this.requestCorrelationId = requestCorrelationId;
    }

    @Override
    public KasperID getRequestCorrelationId() {
        return this.requestCorrelationId;
    }

    @Override
    public void setSessionCorrelationId(KasperID sessionCorrelationId) {
        this.sessionCorrelationId = sessionCorrelationId;
    }

    @Override
    public KasperID getSessionCorrelationId() {
        return this.sessionCorrelationId;
    }

    // ------------------------------------------------------------------------

    public Context child() {
        final DefaultContext newContext = (DefaultContext) super.child();

        newContext.userId = this.userId;
        newContext.userLang = this.userLang;

        newContext.requestCorrelationId = this.requestCorrelationId;
        newContext.sessionCorrelationId = this.sessionCorrelationId;

        return newContext;
    }

}
