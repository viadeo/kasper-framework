package com.viadeo.kasper.context;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.api.ID;
import com.viadeo.kasper.impl.DefaultKasperId;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public interface ImmutableContext extends Serializable {

    int INITIAL_SEQUENCE_INCREMENT = 1;

    String DEFAULT_STRING_ID = "";
    UUID DEFAULT_KASPER_UUID = UUID.fromString("00000000-0000-002a-0000-00000000002a");
    KasperID DEFAULT_KASPER_ID = new DefaultKasperId(DEFAULT_KASPER_UUID);

    String DEFAULT_USER_LANG = "fr";
    String DEFAULT_USER_COUNTRY = "FR";

    String DEFAULT_USER_ID = DEFAULT_STRING_ID;
    String DEFAULT_REQCORR_ID = DEFAULT_STRING_ID;
    String DEFAULT_FUNCORR_ID = DEFAULT_STRING_ID;
    String DEFAULT_SESSCORR_ID = DEFAULT_STRING_ID;

    KasperID DEFAULT_KASPERCORR_ID = DEFAULT_KASPER_ID;

    String DEFAULT_APPLICATION_ID = "UNKNOWN";
    String DEFAULT_SECURITY_TOKEN = "";

    String DEFAULT_FUNNEL_NAME = "";
    String DEFAULT_FUNNEL_VERSION = "";

    String DEFAULT_IP_ADDRESS = "0.0.0.0";

    String USER_ID_SHORTNAME = "userID";
    String UID_SHORTNAME = "userId";
    String ULANG_SHORTNAME = "userLang";
    String UCOUNTRY_SHORTNAME = "userCountry";
    String APPLICATION_ID_SHORTNAME = "appId";
    String REQUEST_CID_SHORTNAME = "corrRequestId";
    String FUNNEL_CID_SHORTNAME = "corrFunnelId";
    String SESSION_CID_SHORTNAME = "corrSessionId";
    String SECURITY_TOKEN_SHORTNAME = "authToken";
    String FUNNEL_NAME_SHORTNAME = "funnelName";
    String FUNNEL_VERS_SHORTNAME = "funnelVersion";
    String IP_ADDRESS_SHORTNAME = "ipAddress";
    String CALL_TYPE = "callType";

    /**
     * The name of the context key when stored in meta data maps
     */
    String METANAME = "context";

    // ------------------------------------------------------------------------

    /**
     * @return the security token used in current context
     */
    String getSecurityToken();

    /**
     * @return the associated ID of the current user
     */
    Optional<ID> getUserID();

    /**
     * @return the caller application id
     */
    String getApplicationId();

    /**
     * @return the user default language (preferred: ISO 639-1)
     */
    String getUserLang();

    /**
     * @return the user default language as locale
     */
    Locale getUserLangAsLocale();


    /**
     * @return the user country (ISO 3166)
     */
    String getUserCountry();

    /**
     * @return the application request correlation id
     */
    String getRequestCorrelationId();

    /**
     * @return the application request correlation id
     */
    String getFunnelCorrelationId();

    /**
     * @return the current funnel name
     */
    String getFunnelName();

    /**
     * @return the current funnel version
     */
    String getFunnelVersion();

    /**
     * @return the application request ip address
     */
    String getIpAddress();

    /**
     * @return the application session correlation id
     */
    String getSessionCorrelationId();

    /**
     * Get a property value
     *
     * @param key the property name
     * @return the optional value
     */
    Optional<Serializable> getProperty(String key);

    /**
     * Checks whether context owns this property by name
     *
     * @param key the property name
     * @return true if this context owns this property name
     */
    boolean hasProperty(String key);

    /**
     * Retrieve all the context properties
     *
     * @return all the context properties
     */
    Map<String, Serializable> getProperties();

    /**
     * Return the context's sequence number
     *
     * @return the sequence number
     */
    int getSequenceIncrement();

    /**
     * Add 1 (one) to the sequence increment
     */
    void incSequence();

    /**
     * @return the context as a map
     */
    Map<String, String> asMap();

    Map<String, String> asMap(Map<String, String> map);

    /**
     * return as a metadata map
     */
    Map<String, ?> asMetaDataMap();

    /**
     * Childify this context
     *
     * @return a new child context
     */
    <C extends ImmutableContext> C child();
}
