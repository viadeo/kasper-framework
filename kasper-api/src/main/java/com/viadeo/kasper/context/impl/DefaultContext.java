// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.context.impl;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.viadeo.kasper.api.ID;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.Tags;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * A default {@link com.viadeo.kasper.context.Context} implementation
 * @see com.viadeo.kasper.context.Context
 *
 */
public class DefaultContext extends AbstractContext {
    private static final long serialVersionUID = -2357451589032314740L;

    // ------------------------------------------------------------------------

    private String userId;
    private ID userID;
    private String userLang;
    private String userCountry;

    private String applicationId;

    private String requestCorrelationId;
    private String funnelCorrelationId;
    private String sessionCorrelationId;

    private String securityToken;

    private String funnelName;
    private String funnelVersion;

    private String ipAddress;

    private Set<String> tags;

    // ------------------------------------------------------------------------

    public DefaultContext() {
        super();

        this.userID = null;
        this.userId = DEFAULT_USER_ID;
        this.userLang = DEFAULT_USER_LANG;
        this.userCountry = DEFAULT_USER_COUNTRY;
        this.requestCorrelationId = DEFAULT_REQCORR_ID;
        this.funnelCorrelationId = DEFAULT_FUNCORR_ID;
        this.sessionCorrelationId = DEFAULT_SESSCORR_ID;
        this.applicationId = DEFAULT_APPLICATION_ID;
        this.securityToken = DEFAULT_SECURITY_TOKEN;
        this.funnelName = DEFAULT_FUNNEL_NAME;
        this.funnelVersion = DEFAULT_FUNNEL_VERSION;
        this.ipAddress = DEFAULT_IP_ADDRESS;
        this.tags = ImmutableSet.of();
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
    public String getUserId() {
        return this.userId;
    }

    @Override
    public DefaultContext setUserId(final String userId) {
        this.userId = checkNotNull(userId);
        return this;
    }

    @Override
    public Optional<ID> getUserID() {
        return Optional.fromNullable(userID);
    }

    @Override
    public Context setUserID(ID id) {
        this.userID = id;
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
    public Locale getUserLangAsLocale() {
        return Locale.forLanguageTag(this.userLang);
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
    public Context setRequestCorrelationId(final String requestCorrelationId) {
        this.requestCorrelationId = checkNotNull(requestCorrelationId);
        return this;
    }

    @Override
    public String getRequestCorrelationId() {
        return this.requestCorrelationId;
    }

    @Override
    public Context setFunnelCorrelationId(final String funnelCorrelationId) {
        this.funnelCorrelationId = checkNotNull(funnelCorrelationId);
        return this;
    }

    @Override
    public String getFunnelCorrelationId() {
        return this.funnelCorrelationId;
    }

    @Override
    public Context setFunnelName(final String funnelName) {
        this.funnelName = checkNotNull(funnelName);
        return this;
    }

    @Override
    public Context setFunnelVersion(final String funnelVersion) {
        this.funnelVersion = checkNotNull(funnelVersion);
        return this;
    }

    @Override
    public String getFunnelName() {
        return funnelName;
    }

    @Override
    public String getFunnelVersion() {
        return funnelVersion;
    }

    @Override
    public Context setSessionCorrelationId(final String sessionCorrelationId) {
        this.sessionCorrelationId = checkNotNull(sessionCorrelationId);
        return this;
    }

    @Override
    public String getSessionCorrelationId() {
        return this.sessionCorrelationId;
    }

    @Override
    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public Optional<String> getFirstIpAddress() {
        Optional<String> result = Optional.absent();
        if (ipAddress.isEmpty()) {
            return result;
        }

        final String[] ipAddressesSplit = ipAddress.split(",");
        if(ipAddressesSplit.length > 0){
            result = Optional.of(ipAddressesSplit[0].trim());
        }

        return result;
    }

    @Override
    public Context setIpAddress(final String ipAddress) {
        this.ipAddress = checkNotNull(ipAddress);
        return this;
    }

    @Override
    public Context setTags(final Set<String> tags) {
        checkNotNull(tags);
        this.tags = ImmutableSet.copyOf(tags);
        return this;
    }

    @Override
    public Set<String> getTags() {
        return tags;
    }

    @Override
    public DefaultContext addTags(final Set<String> additionalTags) {
        checkNotNull(additionalTags);
        final Set<String> mergedTags = Sets.union(this.tags, additionalTags);
        this.tags = ImmutableSet.copyOf(mergedTags);
        return this;
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked") // must be ensured by client
    @Override
    public <C extends Context> C child() {
        final DefaultContext newContext = (DefaultContext) super.child();

        newContext.userID = this.userID;
        newContext.userId = this.userId;
        newContext.userLang = this.userLang;
        newContext.userCountry = this.userCountry;

        newContext.requestCorrelationId = this.requestCorrelationId;
        newContext.funnelCorrelationId = this.funnelCorrelationId;
        newContext.sessionCorrelationId = this.sessionCorrelationId;

        newContext.securityToken = this.securityToken;
        newContext.applicationId = this.applicationId;

        newContext.funnelName = this.funnelName;
        newContext.funnelVersion = this.funnelVersion;

        newContext.ipAddress = this.ipAddress;
        newContext.tags = this.tags;

        return (C) newContext;
    }

    // ------------------------------------------------------------------------

    @Override
    public Map<String, String> asMap() {
        return this.asMap(Maps.<String, String>newHashMap());
    }

    @Override
    public Map<String, String> asMap(final Map<String, String> origMap) {
        final Map<String, String> retMap = super.asMap(origMap);

        retMap.put(USER_ID_SHORTNAME, safeStringObject(this.userID));
        retMap.put(UID_SHORTNAME, safeStringObject(this.userId));
        retMap.put(ULANG_SHORTNAME, safeStringObject(this.userLang));
        retMap.put(UCOUNTRY_SHORTNAME, safeStringObject(this.userCountry));
        retMap.put(REQUEST_CID_SHORTNAME, safeStringObject(this.requestCorrelationId));
        retMap.put(FUNNEL_CID_SHORTNAME, safeStringObject(this.funnelCorrelationId));
        retMap.put(SESSION_CID_SHORTNAME, safeStringObject(this.sessionCorrelationId));
        retMap.put(APPLICATION_ID_SHORTNAME, safeStringObject(this.applicationId));
        retMap.put(SECURITY_TOKEN_SHORTNAME, safeStringObject(this.securityToken));
        retMap.put(FUNNEL_NAME_SHORTNAME, safeStringObject(this.funnelName));
        retMap.put(FUNNEL_VERS_SHORTNAME, safeStringObject(this.funnelVersion));
        retMap.put(IP_ADDRESS_SHORTNAME, safeStringObject(this.ipAddress));
        retMap.put(TAGS_SHORTNAME, Tags.toString(this.tags));

        return retMap;
    }

    @Override
    public Map<String, ?> asMetaDataMap() {
        final DefaultContext that = this;
        return new HashMap<String, Object>() {{
            this.put(METANAME, that);
        }};
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
                && Objects.equal(this.userID, other.userID)
                && Objects.equal(this.userId, other.userId)
                && Objects.equal(this.userLang, other.userLang)
                && Objects.equal(this.userCountry, other.userCountry)
                && Objects.equal(this.applicationId,  other.applicationId)
                && Objects.equal(this.securityToken, other.securityToken)
                && Objects.equal(this.funnelName, other.funnelName)
                && Objects.equal(this.funnelVersion, other.funnelVersion)
                && Objects.equal(this.ipAddress, other.ipAddress)
                && Objects.equal(this.tags, other.tags)
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode( super.hashCode(),
                this.sessionCorrelationId,
                this.funnelCorrelationId,
                this.requestCorrelationId,
                this.applicationId,
                this.funnelName,
                this.funnelVersion,
                this.userID,
                this.userId,
                this.userLang,
                this.userCountry,
                this.securityToken,
                this.ipAddress,
                this.tags
        );
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(super.toString())
                .addValue(this.sessionCorrelationId)
                .addValue(this.funnelCorrelationId)
                .addValue(this.requestCorrelationId)
                .addValue(this.userID)
                .addValue(this.userId)
                .addValue(this.userLang)
                .addValue(this.userCountry)
                .addValue(this.applicationId)
                .addValue(this.securityToken)
                .addValue(this.funnelName)
                .addValue(this.funnelVersion)
                .addValue(this.ipAddress)
                .addValue(this.tags)
                .toString();
    }

}

