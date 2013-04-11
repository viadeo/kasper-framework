// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.context;

import java.io.Serializable;
import java.util.Map;

import com.google.common.base.Optional;
import com.viadeo.kasper.IKasperID;

/**
 *
 * Execution context for commands, events & Queries
 *
 * @see IKasperAgentContext
 * @see IKasperIdentityContext
 *
 */
public interface IContext extends Serializable  {

	/**
	 * The name of the context key when stored in meta data maps
	 */
	static public final String METANAME = "context";

	/**
	 * @return the associated ID of the current user
	 * @see IKasperID
	 */
	IKasperID getUserId();
	
	/**
	 * @param userId the associated ID of the current user
	 * @return the current {@link IKasperIdentityContext} instance
	 */
	IContext setUserId(IKasperID userId);
	
	/**
	 * @return the user default language
	 */
	String getUserLang();

	/**
	 * @param lang user default language
	 */
	void setUserLang(String lang);
	
	/**
	 * Sets a new context property
	 * 
	 * @param key the property name
	 * @param Value the property value
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

}
