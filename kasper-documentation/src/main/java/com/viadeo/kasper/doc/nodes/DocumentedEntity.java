// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.google.common.base.Optional;
import com.viadeo.kasper.ddd.ComponentEntity;
import com.viadeo.kasper.ddd.Entity;
import com.viadeo.kasper.doc.KasperLibrary;
import com.viadeo.kasper.er.RootConcept;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import java.util.Collection;

public class DocumentedEntity extends DocumentedDomainNode {
	private static final long serialVersionUID = -3336007269246172693L;
	
	public static final String TYPE_NAME = "entity";
	public static final String PLURAL_TYPE_NAME = "entities";
	
	private String parent = null;
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
	
	public void setIsAggregate(final Boolean isAggregate) {
		this.isAggregate = isAggregate;
	}
	
	public Boolean isAggregate() {
		return isAggregate;
	}
	
	// ------------------------------------------------------------------------
	
	protected void fillProperties(final Class<? extends Entity> entityClazz) {
		this.properties = new DocumentedBean(entityClazz);
	}
	
	// ------------------------------------------------------------------------
	
	protected void fillParent(final Class<? extends Entity> entityClazz) {
		if (ComponentEntity.class.isAssignableFrom(entityClazz)) {
			
			@SuppressWarnings("unchecked") // Safe
			final Optional<Class<? extends RootConcept>> agr =
				(Optional<Class<? extends RootConcept>>)
					ReflectionGenericsResolver.getParameterTypeFromClass(
						entityClazz, ComponentEntity.class, ComponentEntity.PARENT_ARGUMENT_POSITION);
			
			if (agr.isPresent()) {
				this.parent = agr.get().getSimpleName();
				this.getKasperLibrary().registerAggregateComponent(this.parent, this.getName());
			}
		}
	}
	
	public DocumentedNode getParent() {
		final KasperLibrary kl = this.getKasperLibrary();
		if (null != this.parent) {
			final Optional<DocumentedConcept> conceptParent = kl.getConcept(getDomainName(), this.parent);
			if (conceptParent.isPresent()) {
				return kl.getSimpleNodeFrom(conceptParent.get());
			}
		}
		return null;
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
