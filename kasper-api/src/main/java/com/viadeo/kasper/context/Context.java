// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.context;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * Execution context for commands, events & queries
 *
 */
public interface Context extends Serializable  {

	/**
	 * The name of the context key when stored in meta data maps
	 */
	String METANAME = "context";

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
	 * @return the user default language
	 */
	String getUserLang();

	/**
	 * @param lang user default language
	 */
	void setUserLang(String lang);

    // ------------------------------------------------------------------------

    /**
     * Sets the correlation if associated with an application request
     *
     * For one application-side request, several Kasper actions can be made
     * this correlation id can be used in order to track all Kasper actions
     * made for one application request
     *
     * @param requestCorrelationId the correlation id
     */
    void setRequestCorrelationId(KasperID requestCorrelationId);

    /**
     * @return the application request correlation id
     */
    KasperID getRequestCorrelationId();

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
    void setSessionCorrelationId(KasperID sessionCorrelationId);

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
	void setProperty(String key, Serializable value);
	
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
    Context child();

}
