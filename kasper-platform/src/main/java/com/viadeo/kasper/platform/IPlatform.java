// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform;

import org.axonframework.eventhandling.EventBus;

import com.viadeo.kasper.core.boot.AnnotationRootProcessor;
import com.viadeo.kasper.cqrs.command.ICommandGateway;
import com.viadeo.kasper.cqrs.query.IQueryGateway;
import com.viadeo.kasper.event.IEvent;

/**
 *
 * The Kasper kasper
 */
public interface IPlatform {

	/** Boot */
	
	void boot();
	void setRootProcessor(AnnotationRootProcessor rootProcessor);

	/** Commands */
	
	ICommandGateway getCommandGateway();
	void setCommandGateway(ICommandGateway commandGateway);

	/** Events */
	
	void setEventBus(EventBus eventBus);
	void publishEvent(IEvent event);
	
	/** Queries */
	
	void setQueryGateway(IQueryGateway queryGateway);
	IQueryGateway getQueryGateway();
	
}
