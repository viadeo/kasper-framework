// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.doc.KasperLibrary;
import com.viadeo.kasper.er.Relation;
import com.viadeo.kasper.er.RootConcept;
import com.viadeo.kasper.er.annotation.XBidirectional;
import com.viadeo.kasper.er.annotation.XKasperRelation;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import java.util.StringTokenizer;

public final class DocumentedRelation extends DocumentedEntity {
	private static final long serialVersionUID = -268234449433085371L;
	
	public static final String TYPE_NAME = "relation";
	public static final String PLURAL_TYPE_NAME = "relations";
	
	private Boolean isBidirectional = false;
	private String sourceConceptName = "unknown";
	private String targetConceptName = "unknown";
	
	// ------------------------------------------------------------------------
	
	public DocumentedRelation(final KasperLibrary kl, final Class<? extends Relation<?,?>> relationClazz) {
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);
		
		final XKasperRelation annotation = relationClazz.getAnnotation(XKasperRelation.class);
		final XBidirectional biDirAnno = relationClazz.getAnnotation(XBidirectional.class);
		final boolean annotatedBidirectional = (null != biDirAnno);
		final String label = annotation.label();
				
		// Find if it's an aggregate ------------------------------------------
		final boolean isAggregate = AggregateRoot.class.isAssignableFrom(relationClazz);
		
		// Find associated domain ---------------------------------------------
		final Class<? extends Domain> domain = annotation.domain();
		final String domainName = domain.getSimpleName();
		
		// Find source and target root concepts -------------------------------
		@SuppressWarnings("unchecked") // Safe
		final Optional<Class<? extends RootConcept>> sourceClass =
				(Optional<Class<? extends RootConcept>>)
					ReflectionGenericsResolver.getParameterTypeFromClass(
							relationClazz, Relation.class, Relation.SOURCE_PARAMETER_POSITION);
		
		@SuppressWarnings("unchecked") // Safe
		final Optional<Class<? extends RootConcept>> targetClass =
				(Optional<Class<? extends RootConcept>>)
					ReflectionGenericsResolver.getParameterTypeFromClass(
							relationClazz, Relation.class, Relation.TARGET_PARAMETER_POSITION);
		
		String source = "error";
		String target = "error";
		
		if (sourceClass.isPresent()) {
			source = sourceClass.get().getSimpleName();
		}

		if (targetClass.isPresent()) {
			target = targetClass.get().getSimpleName();
		}		
		
		// Get description ----------------------------------------------------
		String description = annotation.description();
		if (description.isEmpty()) {
			description = String.format("The %s relation between %s and %s", label, source, target);
		}		
		
		//- Set properties ----------------------------------------------------		
		this.setName(relationClazz.getSimpleName());
		this.setLabel(label);
		this.setDescription(description);
		this.setDomainName(domainName);
		this.setIsAggregate(isAggregate);
		this.setIsBidirectional(annotatedBidirectional);
		this.setSourceRootConcept(source);
		this.setTargetRootConcept(target);
		
		if (!isAggregate) {
			fillParent(relationClazz);
		}
		
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
		final Optional<DocumentedConcept> concept = kl.getConcept(this.getDomainName(), this.targetConceptName);
		
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
