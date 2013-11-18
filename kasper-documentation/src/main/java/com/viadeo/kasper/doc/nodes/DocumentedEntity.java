// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.viadeo.kasper.ddd.Entity;
import com.viadeo.kasper.doc.KasperLibrary;

import java.util.Collection;

public class DocumentedEntity extends DocumentedDomainNode {
	private static final long serialVersionUID = -3336007269246172693L;
	
	public static final String TYPE_NAME = "entity";
	public static final String PLURAL_TYPE_NAME = "entities";
	
	private Boolean isAggregate = false;
	
	private DocumentedBean properties = null;
	
	// ------------------------------------------------------------------------
	
	DocumentedEntity(final KasperLibrary kl) { // Used as empty entity to populate
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);
	}
	
	public DocumentedEntity(final KasperLibrary kl, final String type, final String pluralType) {
		super(kl, type, pluralType);
	}
	
	// ------------------------------------------------------------------------
	
	protected void fillProperties(final Class<? extends Entity> entityClazz) {
		this.properties = new DocumentedBean(entityClazz);
	}
	
	// ------------------------------------------------------------------------
	
	public Collection<DocumentedNode> getComponentConcepts() {
		if (isAggregate) {
			final KasperLibrary kl = this.getKasperLibrary();
			return kl.simpleNodesFrom(kl.getConceptComponents(getDomainName(), getName())).values();
		}
		return null;
	}
	
	public Collection<DocumentedNode> getComponentRelations() {
		if (isAggregate) {
			final KasperLibrary kl = this.getKasperLibrary();
			return kl.simpleNodesFrom(kl.getRelationComponents(getDomainName(), getName())).values();
		}
		return null;
	}	
	
	// ------------------------------------------------------------------------
	
	public DocumentedBean getProperties() {
		return this.properties;
	}
	
}
