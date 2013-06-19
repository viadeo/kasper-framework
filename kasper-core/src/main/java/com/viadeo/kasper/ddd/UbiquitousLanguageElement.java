// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.ddd;

/**
 *
 * Semantic type marker for all Kasper DDD ubiquitous language elements
 *
 * From Martin Fowler : 
 * 
 * Ubiquitous Language is the term Eric Evans uses in Domain Driven Designfor the practice of building up a common, rigorous 
 * language between developers and users. This language should be based on the Domain Model used in the software - hence the 
 * need for it to be rigorous, since software doesn't cope well with ambiguity.
 *
 * Evans makes clear that using the ubiquitous language between in conversations with domain experts is an important part of
 * testing it, and hence the domain model. He also stresses that the language (and model) should evolve as the team's 
 * understanding of the domain grows.
 *
 * By using the model-based language pervasively and not being satisfied until it flows, we approach a model that is complete 
 * and comprehensible, made up of simple elements that combine to express complex ideas.
 *
 * 
 * From Eric Evans :
 * 
 * Domain experts should object to terms or structures that are awkward or inadequate to convey domain understanding; 
 * developers should watch for ambiguity or inconsistency that will trip up design.
 *
 */
public interface UbiquitousLanguageElement {

}
