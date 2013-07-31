// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.google.common.base.Optional;
import com.viadeo.kasper.doc.KasperLibrary;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.event.annotation.XKasperEventListener;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;


public final class DocumentedListener extends DocumentedDomainNode {
	private static final long serialVersionUID = 2245288475426783601L;
	
	public static final String TYPE_NAME = "listener";
	public static final String PLURAL_TYPE_NAME = "listeners";
	
	private final String eventName;
	
	// ------------------------------------------------------------------------
	
	public DocumentedListener(final KasperLibrary kl, final Class<? extends EventListener<?>> listenerClazz) {
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);
		
		// Extract event type from listener -----------------------------------
		@SuppressWarnings("unchecked") // Safe
		final Optional<Class<? extends Event>> eventClazz =
				(Optional<Class<? extends Event>>)
					ReflectionGenericsResolver.getParameterTypeFromClass(
						listenerClazz, EventListener.class, EventListener.EVENT_PARAMETER_POSITION);
		
		if (!eventClazz.isPresent()) {
			throw new KasperException("Unable to find event type for listener " + listenerClazz.getClass());
		}
		
		// Find associated domain ---------------------------------------------		
		final String domainName = DocumentedEvent.getDomainFromEventClass(eventClazz.get());
		
		// Get description ----------------------------------------------------
		final XKasperEventListener annotation = listenerClazz.getAnnotation(XKasperEventListener.class);
		String description = annotation.description();
		if (description.isEmpty()) {
			description = String.format("The listener for %s events", eventClazz.get().getSimpleName().replaceAll("Event", ""));
		}
		
		//- Set properties ----------------------------------------------------
		this.eventName = eventClazz.get().getSimpleName();
		this.setName(listenerClazz.getSimpleName());
		this.setDescription(description);
		this.setDomainName(domainName);
		this.getKasperLibrary().registerListener(this, this.eventName);
	}	
	
	// ------------------------------------------------------------------------
	
	public DocumentedNode getEvent() {
		final KasperLibrary kl = this.getKasperLibrary();
		final Optional<DocumentedEvent> concept = kl.getEvent(this.getDomainName(), this.eventName);
		
		if (concept.isPresent()) {
			return kl.getSimpleNodeFrom( concept.get() ); 
		}
		
		return new DocumentedEvent(getKasperLibrary())
			.setDomainName(getDomainName())
			.setName(this.eventName)
			.setDescription("[Not resolved]")
			.toSimpleNode();
	}
	
}
