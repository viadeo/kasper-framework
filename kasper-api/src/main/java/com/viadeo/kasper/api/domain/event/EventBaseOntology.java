// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.domain.event;

/**
 * Base proposed ontology for events
 */
public final class EventBaseOntology {

	private EventBaseOntology() { /* singleton */ }
	
	public static final String CREATED = "created";
	public static final String UPDATED = "updated";
	public static final String DELETED = "deleted";
	
}
