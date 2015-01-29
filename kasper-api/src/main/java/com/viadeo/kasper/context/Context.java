// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.context;

import com.viadeo.kasper.api.ID;

import java.io.Serializable;

/**
 *
 * Execution context for commands, events & queries
 *
 */
@Deprecated
public interface Context extends ImmutableContext  {

    /**
     * @return the associated ID of the current user
     * @deprecated use {@link #getUserID()} instead.
     */
    @Deprecated
    String getUserId();

    /**
     * @param token the security token to be used in current context
     * @return the current {@link Context} instance
     */
    Context setSecurityToken(String token);


    /**
     * @param id the associated ID of the current user
     * @return the current {@link Context} instance
     */
    Context setUserID(ID id);


    /**
     * @param userId the associated ID of the current user
     * @return the current {@link Context} instance
     * @deprecated use {@link #setUserID(com.viadeo.kasper.api.ID)}()} instead.
     */
    @Deprecated
    Context setUserId(String userId);

    /**
     * @param applicationId the caller application id
     * @return the current {@link Context} instance
     */
    Context setApplicationId(String applicationId);


    /**
     * @param lang user default language (preferred: ISO 639-1)
     */
    Context setUserLang(String lang);


    /**
     * @param country user country (ISO 3166)
     */
    Context setUserCountry(String country);

    /**
     * Sets the correlation id associated with an application request
     *
     * For one application-side request, several Kasper actions can be made
     * this correlation id can be used in order to track all Kasper actions
     * made for one application request
     *
     * @param requestCorrelationId the correlation id
     */
    Context setRequestCorrelationId(String requestCorrelationId);

    /**
     * Sets the correlation id associated with an application functional tunnel (funnel)
     *
     * For one session, many functional tunnels can be used by the user
     *
     * @param funnelCorrelationId the correlation id
     */
    Context setFunnelCorrelationId(String funnelCorrelationId);

    /**
     * Sets the funnel name
     *
     * @param funnelName the funnel name of the context
     */
    Context setFunnelName(String funnelName);

    /**
     * Sets the funnel version (funnel declination)
     *
     * @param funnelVersion
     */
    Context setFunnelVersion(String funnelVersion);

    /**
     * Set the ip address of the request
     *
     * @param ipAddress the correlation id
     */
    Context setIpAddress(String ipAddress);


    /**
     * Sets the session correlation id
     *
     * For one application user session, several requests are sent, for each
     * application request several Kasper actions can be made. This correlation id
     * can be used in order to track all Kasper actions made during one application
     * user session
     *
     * @param sessionCorrelationId
     */
    Context setSessionCorrelationId(String sessionCorrelationId);

    /**
     * Sets a new context property
     *
     * @param key the property name
     * @param value the property value
     */
    Context setProperty(String key, Serializable value);

}
