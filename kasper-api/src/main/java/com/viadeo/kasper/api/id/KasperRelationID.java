// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.id;

/**
 *
 * Identify uniquely a Kasper Entity (Concept or Relation)
 * IKasperID is a value object
 *
 */
public interface KasperRelationID extends KasperID {

	/**
	 * @return the enclosing source id
	 */
	KasperID getSourceId();

 	/**
	 * @return the enclosing target id
	 */
	KasperID getTargetId();

}
