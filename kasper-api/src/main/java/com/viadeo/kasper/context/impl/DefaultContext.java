// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.context.impl;

import com.google.common.base.Objects;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.context.Context;

import static com.google.common.base.Preconditions.checkNotNull;

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
    private String userCountry;

    private String applicationId;
    private KasperID requestCorrelationId;
    private KasperID funnelCorrelationId;
    private KasperID sessionCorrelationId;

    private String securityToken;

    // ------------------------------------------------------------------------

    public DefaultContext() {
        super();

        this.userId = DEFAULT_USER_ID;
        this.userLang = DEFAULT_USER_LANG;
        this.userCountry = DEFAULT_USER_COUNTRY;
        this.requestCorrelationId = DEFAULT_REQCORR_ID;
        this.funnelCorrelationId = DEFAULT_FUNCORR_ID;
        this.sessionCorrelationId = DEFAULT_SESSCORR_ID;
        this.applicationId = DEFAULT_APPLICATION_ID;
        this.securityToken = DEFAULT_SECURITY_TOKEN;
    }

    // ------------------------------------------------------------------------

    @Override
    public String getSecurityToken() {
        return this.securityToken;
    }

    @Override
    public Context setSecurityToken(final String token) {
        this.securityToken = checkNotNull(token);
        return this;
    }

    @Override
    public KasperID getUserId() {
        return this.userId;
    }

    @Override
    public DefaultContext setUserId(final KasperID userId) {
        this.userId = checkNotNull(userId);
        return this;
    }

    @Override
    public String getApplicationId() {
        return this.applicationId;
    }

    @Override
    public Context setApplicationId(final String applicationId) {
        this.applicationId = checkNotNull(applicationId);
        return this;
    }

    @Override
    public String getUserLang() {
        return this.userLang;
    }

    @Override
    public Context setUserLang(final String userLang) {
        this.userLang = checkNotNull(userLang);
        return this;
    }

    @Override
    public String getUserCountry() {
        return this.userCountry;
    }

    @Override
    public Context setUserCountry(final String userCountry) {
        this.userCountry = checkNotNull(userCountry);
        return this;
    }

    @Override
    public Context setRequestCorrelationId(KasperID requestCorrelationId) {
        this.requestCorrelationId = checkNotNull(requestCorrelationId);
        return this;
    }

    @Override
    public KasperID getRequestCorrelationId() {
        return this.requestCorrelationId;
    }

    @Override
    public Context setFunnelCorrelationId(final KasperID funnelCorrelationId) {
        this.funnelCorrelationId = checkNotNull(funnelCorrelationId);
        return this;
    }

    @Override
    public KasperID getFunnelCorrelationId() {
        return this.funnelCorrelationId;
    }

    @Override
    public Context setSessionCorrelationId(KasperID sessionCorrelationId) {
        this.sessionCorrelationId = checkNotNull(sessionCorrelationId);
        return this;
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
        newContext.userCountry = this.userCountry;

        newContext.requestCorrelationId = this.requestCorrelationId;
        newContext.funnelCorrelationId = this.funnelCorrelationId;
        newContext.sessionCorrelationId = this.sessionCorrelationId;

        newContext.securityToken = this.securityToken;
        newContext.applicationId = this.applicationId;

        return newContext;
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(final Object obj) {
        if (null == obj) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final DefaultContext other = (DefaultContext) obj;

        return  super.equals(obj)
                && Objects.equal(this.sessionCorrelationId, other.sessionCorrelationId)
                && Objects.equal(this.funnelCorrelationId, other.funnelCorrelationId)
                && Objects.equal(this.requestCorrelationId, other.requestCorrelationId)
                && Objects.equal(this.userId, other.userId)
                && Objects.equal(this.userLang, other.userLang)
                && Objects.equal(this.userCountry, other.userCountry)
                && Objects.equal(this.applicationId,  other.applicationId)
                && Objects.equal(this.securityToken, other.securityToken);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode( super.hashCode(),
                this.sessionCorrelationId, this.funnelCorrelationId, this.requestCorrelationId,
                this.applicationId,
                this.userId, this.userLang, this.userCountry, this.securityToken);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(super.toString())
                .addValue(this.sessionCorrelationId)
                .addValue(this.funnelCorrelationId)
                .addValue(this.requestCorrelationId)
                .addValue(this.userId)
                .addValue(this.userLang)
                .addValue(this.userCountry)
                .addValue(this.applicationId)
                .addValue(this.securityToken)
                .toString();
    }

}
