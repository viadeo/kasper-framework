// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd;

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
 * @see UbiquitousLanguageElement
 */
public interface Entity extends Serializable, UbiquitousLanguageElement {

}
