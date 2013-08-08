// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.google.common.base.Optional;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.doc.KasperLibrary;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.domain.DomainEvent;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import java.util.Collection;


public final class DocumentedEvent extends DocumentedDomainNode {
	private static final long serialVersionUID = 6817858609739438236L;
	
	public static final String TYPE_NAME = "event";
	public static final String PLURAL_TYPE_NAME = "events";
	
	private String action = "unknown";
	
	private DocumentedBean properties = null;
	
	// ------------------------------------------------------------------------
	
	DocumentedEvent(final KasperLibrary kl) { // Used as empty event to populate
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);
	}
	
	public DocumentedEvent(final KasperLibrary kl, final Class<? extends Event> eventClazz) {
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);
		
		final String domainName = getDomainFromEventClass(eventClazz);
		
		// Get description ----------------------------------------------------
		final XKasperEvent annotation = eventClazz.getAnnotation(XKasperEvent.class);
		final String description;
  		final String annotatedAction;
		if (annotation != null) {
              description = annotation.description();
            annotatedAction = annotation.action();
        } else {
			description = String.format("The %s event", eventClazz.getSimpleName().replaceAll("Event", ""));
            annotatedAction = "";
		}


		
		// Set properties -----------------------------------------------------
		this.setAction(annotatedAction);
		this.setName(eventClazz.getSimpleName());
		this.setDescription(description);
		this.setDomainName(domainName);		
		this.properties = new DocumentedBean(eventClazz);
	}	
	
	// ------------------------------------------------------------------------
	
	public void setAction(final String action) {
		this.action = action;
	}
	
	// ------------------------------------------------------------------------
	
	public String getAction() {
		return this.action;
	}
	
	
	// ------------------------------------------------------------------------
	
	public static String getDomainFromEventClass(final Class<?> eventClazz) {

        if (DomainEvent.class.isAssignableFrom(eventClazz)) {
            @SuppressWarnings("unchecked") // Safe
            final Optional<Class<? extends Domain>> domainClazz =
                    (Optional<Class<? extends Domain>>)
                            ReflectionGenericsResolver.getParameterTypeFromClass(
                                    eventClazz,
                                    DomainEvent.class,
                                    DomainEvent.DOMAIN_PARAMETER_POSITION);

            if (domainClazz.isPresent()) {
                return domainClazz.get().getSimpleName();
            }
        }

		return "Unknown";
	}	
	
	// ------------------------------------------------------------------------
	
	public String getLabel() {
		if (null == this.label) {
			return this.getName().replaceAll("Event", "");
		}
		return super.getLabel();
	}		
	
	// ------------------------------------------------------------------------
	
	public Collection<DocumentedNode> getListeners() {
		final KasperLibrary kl = this.getKasperLibrary();
		return kl.simpleNodesFrom( kl.getListenersForEvent(getDomainName(), getName()) ).values();
	}
	
	// ------------------------------------------------------------------------
	
	public DocumentedBean getProperties() {
		return this.properties;
	}
	
}
