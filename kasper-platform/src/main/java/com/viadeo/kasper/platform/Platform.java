// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform;

import com.viadeo.kasper.core.boot.AnnotationRootProcessor;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.event.Event;
import org.axonframework.eventhandling.EventBus;

/**
 *
 * The Kasper platform
 */
public interface Platform {

	/** Boot */
	
	void boot();
	void setRootProcessor(AnnotationRootProcessor rootProcessor);
    AnnotationRootProcessor getRootProcessor();

	/** Commands */
	
	CommandGateway getCommandGateway();
	void setCommandGateway(CommandGateway commandGateway);

	/** Events */
	
	void setEventBus(EventBus eventBus);
	void publishEvent(Event event);
	
	/** Queries */
	
	void setQueryGateway(QueryGateway queryGateway);
	QueryGateway getQueryGateway();
	
}
