// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.resolvers.EventListenerResolver;
import com.viadeo.kasper.doc.KasperLibrary;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.event.annotation.XKasperEventListener;

public final class DocumentedListener extends DocumentedDomainNode {
	private static final long serialVersionUID = 2245288475426783601L;
	
	public static final String TYPE_NAME = "listener";
	public static final String PLURAL_TYPE_NAME = "listeners";
	
	private final String eventName;
	
	// ------------------------------------------------------------------------
	
	public DocumentedListener(final KasperLibrary kl, final Class<? extends EventListener> listenerClazz) {
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);

        final EventListenerResolver resolver =
                this.getKasperLibrary().getResolverFactory().getEventListenerResolver();

		// Extract event type from listener -----------------------------------
		@SuppressWarnings("unchecked") // Safe
		final Class<? extends Event> eventClazz = resolver.getEventClass(listenerClazz);

		// Find associated domain ---------------------------------------------		
		final String domainName = DocumentedEvent.getDomainFromEventClass(
                this.getKasperLibrary().getResolverFactory().getEventResolver(), eventClazz);
		
		// Get description ----------------------------------------------------
		final String description = resolver.getDescription(listenerClazz);

		//- Set properties ----------------------------------------------------
		this.eventName = eventClazz.getSimpleName();
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
