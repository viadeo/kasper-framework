// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.platform.impl;

import java.util.Map;

import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.EventBus;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.viadeo.kasper.context.IContext;
import com.viadeo.kasper.core.boot.AnnotationRootProcessor;
import com.viadeo.kasper.cqrs.command.ICommandGateway;
import com.viadeo.kasper.cqrs.query.IQueryGateway;
import com.viadeo.kasper.event.IEvent;
import com.viadeo.kasper.platform.IPlatform;

public class KasperPlatform implements IPlatform {

	protected ICommandGateway commandGateway;
	protected IQueryGateway queryGateway;
	protected AnnotationRootProcessor rootProcessor;
	protected EventBus eventBus;
	private volatile Boolean _booted = false;

	// ------------------------------------------------------------------------

	@Override
	public void boot() {
	    synchronized (_booted) {
	        if (!_booted) {
	            this.rootProcessor.boot();
	            _booted = true;
	        }
        }
	}

	@Override
	public ICommandGateway getCommandGateway() {
		return this.commandGateway;
	}

	public AnnotationRootProcessor getRootProcessor() {
		return this.rootProcessor;
	}

	@Override
	public IQueryGateway getQueryGateway() {
		return this.queryGateway;
	}

	@Override
	public void publishEvent(final IEvent event) {
		Preconditions.checkNotNull(event);
		Preconditions.checkState(event.getContext().isPresent(), "Context must be present !");

		final IContext context = event.getContext().get();
		final Map<String, Object> metaData = Maps.newHashMap();
		metaData.put(IContext.METANAME, Preconditions.checkNotNull(context));

		final GenericEventMessage<IEvent> eventMessageAxon = 
				new GenericEventMessage<IEvent>(event, metaData);

		this.eventBus.publish(eventMessageAxon);
	}

	// ------------------------------------------------------------------------

	@Override
	public void setCommandGateway(final ICommandGateway commandGateway) {
		this.commandGateway = Preconditions.checkNotNull(commandGateway);
	}

	@Override
	public void setRootProcessor(final AnnotationRootProcessor rootProcessor) {
		this.rootProcessor = Preconditions.checkNotNull(rootProcessor);
	}

	@Override
	public void setQueryGateway(final IQueryGateway queryGateway) {
		this.queryGateway = Preconditions.checkNotNull(queryGateway);
	}

	@Override
	public void setEventBus(final EventBus eventBus) {
		this.eventBus = Preconditions.checkNotNull(eventBus);
	}

}
