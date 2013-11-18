// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import com.viadeo.kasper.core.resolvers.RelationResolver;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.doc.KasperLibrary;
import com.viadeo.kasper.er.Concept;
import com.viadeo.kasper.er.Relation;

import java.util.StringTokenizer;

public final class DocumentedRelation extends DocumentedEntity {
	private static final long serialVersionUID = -268234449433085371L;
	
	public static final String TYPE_NAME = "relation";
	public static final String PLURAL_TYPE_NAME = "relations";
	
	private Boolean isBidirectional = false;
	private String sourceConceptName = "unknown";
	private String targetConceptName = "unknown";
	
	// ------------------------------------------------------------------------
	
	public DocumentedRelation(final KasperLibrary kl, final Class<? extends Relation> relationClazz) {
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);
		
		// Find if it's an aggregate ------------------------------------------
		final boolean isAggregate = AggregateRoot.class.isAssignableFrom(relationClazz);
		
		// Find associated domain ---------------------------------------------
        final RelationResolver resolver = this.getKasperLibrary().getResolverFactory().getRelationResolver();
		final String domainName = resolver.getDomainClass(relationClazz).get().getSimpleName();
        final String label = resolver.getLabel(relationClazz);
        final String description = resolver.getDescription(relationClazz);
 		final boolean annotatedBidirectional = resolver.isBidirectional(relationClazz);

		// Find source and target root concepts -------------------------------
		@SuppressWarnings("unchecked") // Safe
		final Class<? extends Concept> sourceClass =
                resolver.getSourceEntityClass(relationClazz);

		@SuppressWarnings("unchecked") // Safe
		final Class<? extends Concept> targetClass =
                resolver.getTargetEntityClass(relationClazz);

        final String source = sourceClass.getSimpleName();
		final String target = targetClass.getSimpleName();

		//- Set properties ----------------------------------------------------
		this.setName(relationClazz.getSimpleName());
		this.setLabel(label);
		this.setDescription(description);
		this.setDomainName(domainName);
		this.setIsBidirectional(annotatedBidirectional);
		this.setSourceRootConcept(source);
		this.setTargetRootConcept(target);
		
		fillProperties(relationClazz);
	}	

	// ------------------------------------------------------------------------
	
	public void setIsBidirectional(final boolean isBidirectional) {
		this.isBidirectional = isBidirectional;
	}
	
	public void setSourceRootConcept(final String source) {
		this.sourceConceptName = source;
	}
	
	public void setTargetRootConcept(final String target) {
		this.targetConceptName = target;
	}
	
	// ------------------------------------------------------------------------
	
	public Boolean isBidirectional() {
		return isBidirectional;
	}
	
	// --
	
	public DocumentedNode getSourceConcept() {
		final KasperLibrary kl = this.getKasperLibrary();
		final Optional<DocumentedConcept> concept = kl.getConcept(this.getDomainName(), this.sourceConceptName);
		
		if (concept.isPresent()) {
			return kl.getSimpleNodeFrom( concept.get() ); 
		}
		
		return new DocumentedConcept(getKasperLibrary())
			.setDomainName(getDomainName())
			.setName(this.sourceConceptName)
			.setDescription("[Not resolved]")
			.toSimpleNode();
	}
	
	// --
	
	public DocumentedNode getTargetConcept() {
		final KasperLibrary kl = this.getKasperLibrary();
		final Optional<DocumentedConcept> concept = kl.getConcept(this.getDomainName(),
                this.targetConceptName);
		
		if (concept.isPresent()) {
			return kl.getSimpleNodeFrom( concept.get() ); 
		}
		
		return new DocumentedConcept(getKasperLibrary())
			.setDomainName(getDomainName())
			.setName(this.targetConceptName)
			.setDescription("[Not resolved]")
			.toSimpleNode();
	}
	
	// ------------------------------------------------------------------------
	
	@JsonIgnore
	public String getSourceConceptName() {
		return this.sourceConceptName;
	}
	
	@JsonIgnore
	public String getTargetConceptName() {
		return this.targetConceptName;
	}
	
	// ------------------------------------------------------------------------
	
	public String getLabel() {
		if (null == this.label) {
			final StringTokenizer st = new StringTokenizer(this.getName(), "_"); 
			if( st.countTokens() == 2) { 
				st.nextToken(); 
				return st.nextToken();  
			} 
		}
		return super.getLabel();
	}

}
