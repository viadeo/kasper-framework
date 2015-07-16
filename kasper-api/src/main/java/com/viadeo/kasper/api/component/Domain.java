// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.component;


/**
 *
 * near of a DDD Bounded Context
 * 
 * All entities in this package are related to a domain by generalization
 * 
 * A domain communicates with other domains in three ways :
 * - handling commands
 * - sending domain events
 * - listening domain or external events
 *
 * In Kasper, query handlers are also binded to a domain, which can be the same than a command
 * domain, or can be a query-only dedicated domain.
 *
 ******
 * From domaindrivendesign.org :
 * 
 * Bounded Context
 * Definition: The delimited applicability of a particular model. BOUNDING CONTEXTS gives team members a clear and shared
 * understanding of what has to be consistent and what can develop independently.
 *
 * Problem: Multiple models are in play on any large project. Yet when code based on distinct models is combined,
 * software becomes buggy, unreliable, and difficult to understand. Communication among team members becomes confused.
 * It is often unclear in what context a model should not be applied.
 * 
 * Solution: Explicitly define the context within which a model applies. Explicitly set boundaries in terms of team
 * organization, usage within specific parts of the application, and physical manifestations such as code bases and
 * database schemas. Keep the model strictly consistent within these bounds, but don?t be distracted or confused by issues
 * outside.
 ******
 *
 */
public interface Domain {

}
