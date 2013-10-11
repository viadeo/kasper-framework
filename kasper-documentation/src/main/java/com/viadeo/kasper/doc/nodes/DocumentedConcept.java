// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.viadeo.kasper.core.resolvers.ConceptResolver;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.doc.KasperLibrary;
import com.viadeo.kasper.er.Concept;
import com.viadeo.kasper.er.annotation.XKasperConcept;
import com.viadeo.kasper.event.Event;
import org.axonframework.eventhandling.annotation.EventHandler;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class DocumentedConcept extends DocumentedEntity {
	private static final long serialVersionUID = 3750351443738850009L;
	
	public static final String TYPE_NAME = "concept";
	public static final String PLURAL_TYPE_NAME = "concepts";
	
	private final List<String> sourceEvents = Lists.newArrayList(); 
	
	// ------------------------------------------------------------------------
	
	DocumentedConcept(final KasperLibrary kl) { // Used as empty concept to populate
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);
	}
	
	public DocumentedConcept(final KasperLibrary kl, final Class<? extends Concept> conceptClazz) {
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);
		
		final boolean isAggregate = AggregateRoot.class.isAssignableFrom(conceptClazz);
		
		// Find associated domain ---------------------------------------------
        final ConceptResolver resolver = this.getKasperLibrary().getResolverFactory().getConceptResolver();
 		final String domainName = resolver.getDomainClass(conceptClazz).get().getSimpleName();
        final String description = resolver.getDescription(conceptClazz);
        final String label = resolver.getLabel(conceptClazz);

		// Set properties -----------------------------------------------------
		this.setIsAggregate(isAggregate);
		this.setLabel(label);
		this.setName(conceptClazz.getSimpleName());
		this.setDomainName(domainName);
		this.setDescription(description);
		
		if (isAggregate) {
			fillSourceEvents(conceptClazz);
		} else {
			fillParent(conceptClazz);
		}
		
		fillProperties(conceptClazz);
	}	
	
	// --
	
	private void fillSourceEvents(final Class<? extends Concept> conceptClazz) {
        final Collection<Class<? extends Event>> listenedEvents =
                this.getKasperLibrary().getResolverFactory().getEntityResolver().getListenedSourceEvents(conceptClazz);

		for (final Class<? extends Event> eventClazz : listenedEvents) {
		    sourceEvents.add(eventClazz.getSimpleName());
		}
	}
	
	// ------------------------------------------------------------------------
	
	public Collection<DocumentedNode> getSourceRelations() {
		final KasperLibrary kl = this.getKasperLibrary();
		return kl.simpleNodesFrom(kl.getSourceConceptRelations(getName())).values();
	}
	
	public Collection<DocumentedNode> getTargetRelations() {
		final KasperLibrary kl = this.getKasperLibrary();
		return kl.simpleNodesFrom(kl.getTargetConceptRelations(getName())).values();
	}
	
	// ------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public Collection<DocumentedNode> getSourceEvents() {		
		if (this.sourceEvents.size() == 0) {
			return Collections.EMPTY_LIST;
		}
		
		final KasperLibrary kl = this.getKasperLibrary();
		final List<DocumentedEvent> events = Lists.newArrayList();
		for (final String eventName : this.sourceEvents) {
			final Optional<DocumentedEvent> event = kl.getEvent(getDomainName(), eventName);
			if (event.isPresent()) {
				events.add(event.get());
			}
		}
		
		return kl.simpleNodesFrom(events).values();
	}
	
}
