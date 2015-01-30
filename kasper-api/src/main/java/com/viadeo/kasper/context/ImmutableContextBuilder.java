package com.viadeo.kasper.context;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.ID;

import java.io.Serializable;
import java.util.Map;

public interface ImmutableContextBuilder<C extends ImmutableContext> {

    C build();

    /**
     * @param id the associated ID of the current user
     */
    ImmutableContextBuilder withUserID(ID id);

    /**
     * @param token the security token to be used in current context
     */
    ImmutableContextBuilder withSecurityToken(String token);

    /**
     * @param applicationId the caller application id
     */
    ImmutableContextBuilder withApplicationId(String applicationId);

    /**
     * @param lang user default language (preferred: ISO 639-1)
     */
    ImmutableContextBuilder withUserLang(String lang);

    /**
     * @param country user country (ISO 3166)
     */
    ImmutableContextBuilder withUserCountry(String country);

    /**
     * Sets the correlation id associated with an application request
     * <p/>
     * For one application-side request, several Kasper actions can be made
     * this correlation id can be used in order to track all Kasper actions
     * made for one application request
     *
     * @param requestCorrelationId the correlation id
     */
    ImmutableContextBuilder withRequestCorrelationId(String requestCorrelationId);

    /**
     * Sets the correlation id associated with an application functional tunnel (funnel)
     * <p/>
     * For one session, many functional tunnels can be used by the user
     *
     * @param funnelCorrelationId the correlation id
     */
    ImmutableContextBuilder withFunnelCorrelationId(String funnelCorrelationId);

    /**
     * Sets the funnel name
     *
     * @param funnelName the funnel name of the context
     */
    ImmutableContextBuilder withFunnelName(String funnelName);

    /**
     * Sets the funnel version (funnel declination)
     *
     * @param funnelVersion
     */
    ImmutableContextBuilder withFunnelVersion(String funnelVersion);

    /**
     * Set the ip address of the request
     *
     * @param ipAddress the correlation id
     */
    ImmutableContextBuilder withIpAddress(String ipAddress);

    /**
     * Sets the session correlation id
     * <p/>
     * For one application user session, several requests are sent, for each
     * application request several Kasper actions can be made. This correlation id
     * can be used in order to track all Kasper actions made during one application
     * user session
     *
     * @param sessionCorrelationId
     */
    ImmutableContextBuilder withSessionCorrelationId(String sessionCorrelationId);

    /**
     * Sets a new context property
     *
     * @param key   the property name
     * @param value the property value
     */
    ImmutableContextBuilder withProperty(String key, Serializable value);

    ImmutableContextBuilder withProperties(Map<String, Serializable> properties);

    /**
     * Sets all values from the context
     *
     * @param context
     */
    ImmutableContextBuilder withContext(C context);

    /**
     * Set a validator
     *
     * @param validator
     * @return
     */
    ImmutableContextBuilder withValidator(ContextValidator<C> validator);

    Optional<ContextValidator<C>> getValidator();

    ImmutableContextBuilder withSequenceIncrement(int sequenceIncrement);
}
