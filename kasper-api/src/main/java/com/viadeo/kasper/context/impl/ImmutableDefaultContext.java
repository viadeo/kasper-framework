package com.viadeo.kasper.context.impl;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.ID;
import com.viadeo.kasper.context.ImmutableContext;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ImmutableDefaultContext extends AbstractImmutableContext {

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


    public ImmutableDefaultContext() {
        super();

        this.userID = null;
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
    }

    ImmutableDefaultContext(ID userID, String userLang, String userCountry, String applicationId,
                            String requestCorrelationId, String funnelCorrelationId, String sessionCorrelationId,
                            String securityToken, String funnelName, String funnelVersion, String ipAddress, int sequenceIncrement) {
        this.userID = userID;
        this.userLang = userLang;
        this.userCountry = userCountry;
        this.applicationId = applicationId;
        this.requestCorrelationId = requestCorrelationId;
        this.funnelCorrelationId = funnelCorrelationId;
        this.sessionCorrelationId = sessionCorrelationId;
        this.securityToken = securityToken;
        this.funnelName = funnelName;
        this.funnelVersion = funnelVersion;
        this.ipAddress = ipAddress;
        this.sequenceIncrement = sequenceIncrement;
    }

    @Override
    public String getSecurityToken() {
        return securityToken;
    }

    @Override
    public Optional<ID> getUserID() {
        return Optional.fromNullable(userID);
    }

    @Override
    public String getApplicationId() {
        return applicationId;
    }

    @Override
    public String getUserLang() {
        return userLang;
    }

    @Override
    public Locale getUserLangAsLocale() {
        return Locale.forLanguageTag(this.userLang);
    }

    @Override
    public String getUserCountry() {
        return userCountry;
    }

    @Override
    public String getRequestCorrelationId() {
        return requestCorrelationId;
    }

    @Override
    public String getFunnelCorrelationId() {
        return funnelCorrelationId;
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
    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public String getSessionCorrelationId() {
        return sessionCorrelationId;
    }

    @Override
    public Map<String, ?> asMetaDataMap() {
        final ImmutableDefaultContext that = this;
        return new HashMap<String, Object>() {{
            this.put(METANAME, that);
        }};
    }

    @Override
    public Map<String, String> asMap() {
        return this.asMap(Maps.<String, String>newHashMap());
    }

    @Override
    public Map<String, String> asMap(final Map<String, String> origMap) {
        final Map<String, String> retMap = super.asMap(origMap);

        retMap.put(USER_ID_SHORTNAME, safeStringObject(this.userID));
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

        return retMap;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ImmutableContext child() {

        return new ImmutableDefaultContextBuilder()
                .withContext(this)
                .withSequenceIncrement(this.getSequenceIncrement() + 1)
                .build();
    }
}
