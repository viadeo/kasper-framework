// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.viadeo.kasper.core.resolvers.EntityResolver;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.Entity;
import com.viadeo.kasper.doc.KasperLibrary;
import com.viadeo.kasper.er.Concept;

import java.util.Collection;
import java.util.List;

public class DocumentedEntity extends DocumentedDomainNode {
	private static final long serialVersionUID = -3336007269246172693L;
	
	public static final String TYPE_NAME = "entity";
	public static final String PLURAL_TYPE_NAME = "entities";
	
	private DocumentedBean properties = null;
	
	// ------------------------------------------------------------------------
	
	DocumentedEntity(final KasperLibrary kl) { // Used as empty entity to populate
		this(kl, null, TYPE_NAME, PLURAL_TYPE_NAME);
	}
	
	public DocumentedEntity(final KasperLibrary kl, final Class<? extends Entity> entityClazz, final String type, final String pluralType) {
		super(kl, type, pluralType);

        if (null != entityClazz) {
            final EntityResolver resolver = this.getKasperLibrary().getResolverFactory().getEntityResolver();
            if (AggregateRoot.class.isAssignableFrom(entityClazz)) {
                @SuppressWarnings("unchecked") // Safe
                final List<Class<? extends Concept>> links = resolver.getComponentConcepts((Class<? extends AggregateRoot>) entityClazz);
                for (final Class<? extends Concept> link : links) {
                    kl.registerAggregateComponent(entityClazz.getSimpleName(), link.getSimpleName());
                }
            }
        }
	}
	
	// ------------------------------------------------------------------------
	
	protected void fillProperties(final Class<? extends Entity> entityClazz) {
		this.properties = new DocumentedBean(entityClazz);
	}
	
	// ------------------------------------------------------------------------
	
	public Collection<DocumentedNode> getComponentConcepts() {
        final KasperLibrary kl = this.getKasperLibrary();
        return kl.simpleNodesFrom(kl.getConceptComponents(getDomainName(), getName())).values();
	}
	
	public Collection<DocumentedNode> getComponentRelations() {
        final KasperLibrary kl = this.getKasperLibrary();
        return kl.simpleNodesFrom(kl.getRelationComponents(getDomainName(), getName())).values();
	}
	
	// ------------------------------------------------------------------------
	
	public DocumentedBean getProperties() {
		return this.properties;
	}
	
}
