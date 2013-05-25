// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd;

import com.viadeo.kasper.IDomain;
import com.viadeo.kasper.IKasperID;
import com.viadeo.kasper.locators.IDomainLocator;
import org.joda.time.DateTime;

import java.io.Serializable;

/**
 *
 * The base domain entity
 *
 ******
 * An object that is not defined by its attributes, but rather by a thread of continuity and its identity.
 * 
 * Example: Most airlines distinguish each seat uniquely on every flight. Each seat is an entity in this context.
 * However, Southwest Airlines (or EasyJet/RyanAir for Europeans) does not distinguish between every seat; all seats are
 * the same. In this context, a seat is actually a value object.
 * 
 ******
 * 
 * @see IUbiquitousLanguageElement
 */
public interface IEntity extends Serializable, IUbiquitousLanguageElement {

	/**
	 * @return the entity's domain
	 */
	IDomain getDomain();

	/**
	 * Sets the entity's referent domain locator
	 * 
	 * @param domainLocator
	 */
	void setDomainLocator(IDomainLocator domainLocator);

	/**
	 * @return the entity id
	 */
	<I extends IKasperID> I getEntityId();

	/**
	 * @return the entity's creation date
	 */
	DateTime getCreationDate();
	
	/**
	 * @return the entity's last modification date
	 */
	DateTime getModificationDate();
	
}
