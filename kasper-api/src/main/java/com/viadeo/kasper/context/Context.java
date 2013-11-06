// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.context;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.impl.DefaultKasperId;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 *
 * Execution context for commands, events & queries
 *
 */
public interface Context extends Serializable  {

    int INITIAL_SEQUENCE_INCREMENT = 1;
    UUID DEFAULT_KASPER_UUID = UUID.fromString("00000000-0000-002a-0000-00000000002a");
    KasperID DEFAULT_KASPER_ID = new DefaultKasperId(DEFAULT_KASPER_UUID);

    String DEFAULT_USER_LANG = "fr";
    String DEFAULT_USER_COUNTRY = "FR";

    KasperID DEFAULT_USER_ID = DEFAULT_KASPER_ID;
    KasperID DEFAULT_REQCORR_ID = DEFAULT_KASPER_ID;
    KasperID DEFAULT_FUNCORR_ID = DEFAULT_KASPER_ID;
    KasperID DEFAULT_SESSCORR_ID = DEFAULT_KASPER_ID;
    KasperID DEFAULT_KASPERCORR_ID = DEFAULT_KASPER_ID;

    String DEFAULT_APPLICATION_ID = "UNKNOWN";
    String DEFAULT_SECURITY_TOKEN = "unauthenticated";

    // ------------------------------------------------------------------------

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
     * @param token the security token to be used in current context
     * @return the current {@link Context} instance
     */
    Context setSecurityToken(String token);

    // ------------------------------------------------------------------------

	/**
	 * @return the associated ID of the current user
	 * @see com.viadeo.kasper.KasperID
	 */
	KasperID getUserId();
	
	/**
	 * @param userId the associated ID of the current user
	 * @return the current {@link Context} instance
	 */
	Context setUserId(KasperID userId);

    // ------------------------------------------------------------------------

	/**
	 * @return the caller application id
	 */
	String getApplicationId();

	/**
	 * @param applicationId the caller application id
	 * @return the current {@link Context} instance
	 */
	Context setApplicationId(String applicationId);

    // ------------------------------------------------------------------------

	/**
	 * @return the user default language (ISO 639-1)
	 */
	String getUserLang();

	/**
	 * @param lang user default language (ISO 639-1)
	 */
	Context setUserLang(String lang);

    // ------------------------------------------------------------------------

	/**
	 * @return the user country (ISO 3166)
	 */
	String getUserCountry();

	/**
	 * @param country user country (ISO 3166)
	 */
	Context setUserCountry(String country);

    // ------------------------------------------------------------------------

    /**
     * Sets the correlation id associated with an application request
     *
     * For one application-side request, several Kasper actions can be made
     * this correlation id can be used in order to track all Kasper actions
     * made for one application request
     *
     * @param requestCorrelationId the correlation id
     */
    Context setRequestCorrelationId(KasperID requestCorrelationId);

    /**
     * @return the application request correlation id
     */
    KasperID getRequestCorrelationId();

    // ------------------------------------------------------------------------

    /**
     * Sets the correlation id associated with an application functional tunnel (funnel)
     *
     * For one session, many functional tunnels can be used by the user
     *
     * @param funnelCorrelationId the correlation id
     */
    Context setFunnelCorrelationId(KasperID funnelCorrelationId);

    /**
     * @return the application request correlation id
     */
    KasperID getFunnelCorrelationId();

    // ------------------------------------------------------------------------

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
    Context setSessionCorrelationId(KasperID sessionCorrelationId);

    /**
     * @return the application session correlation id
     */
    KasperID getSessionCorrelationId();

    // ------------------------------------------------------------------------
	
	/**
	 * Sets a new context property
	 * 
	 * @param key the property name
	 * @param value the property value
	 */
	Context setProperty(String key, Serializable value);
	
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

    // ------------------------------------------------------------------------

    /**
     * Return the context's sequence number
     *
     * @return the sequence number
     */
    int getSequenceIncrement();

    /**
     * Childify this context
     *
     * @return a new child context
     */
    <C extends Context> C child();

    // ------------------------------------------------------------------------

    /**
     * @return the context as a map
     */
    Map<String, Serializable> asMap();
    Map<String, Serializable> asMap(Map<String, Serializable> map);


}
