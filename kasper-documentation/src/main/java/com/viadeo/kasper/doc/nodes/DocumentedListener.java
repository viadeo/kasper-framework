// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.google.common.base.Optional;
import com.viadeo.kasper.doc.KasperLibrary;
import com.viadeo.kasper.event.IEvent;
import com.viadeo.kasper.event.IEventListener;
import com.viadeo.kasper.event.annotation.XKasperEventListener;
import com.viadeo.kasper.exception.KasperRuntimeException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;


public final class DocumentedListener extends AbstractDocumentedDomainNode {
	private static final long serialVersionUID = 2245288475776783601L;
	
	static public final String TYPE_NAME = "listener";
	static public final String PLURAL_TYPE_NAME = "listeners";
	
	private final String eventName;
	
	// ------------------------------------------------------------------------
	
	public DocumentedListener(final KasperLibrary kl, final Class<? extends IEventListener<?>> listenerClazz) {
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);
		
		// Extract event type from listener -----------------------------------
		@SuppressWarnings("unchecked") // Safe
		final Optional<Class<? extends IEvent>> eventClazz =  
				(Optional<Class<? extends IEvent>>) 
					ReflectionGenericsResolver.getParameterTypeFromClass(
						listenerClazz, IEventListener.class, IEventListener.EVENT_PARAMETER_POSITION);
		
		if (!eventClazz.isPresent()) {
			throw new KasperRuntimeException("Unable to find event type for listener " + listenerClazz.getClass());
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
