// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.doc.nodes;

import com.google.common.base.Optional;
import com.viadeo.kasper.Domain;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.Repository;
import com.viadeo.kasper.ddd.annotation.XKasperDomain;
import com.viadeo.kasper.ddd.annotation.XKasperRepository;
import com.viadeo.kasper.test.doc.KasperLibrary;
import com.viadeo.kasper.er.annotation.XKasperConcept;
import com.viadeo.kasper.er.annotation.XKasperRelation;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;


public final class DocumentedRepository extends DocumentedDomainNode {
	private static final long serialVersionUID = 2245288475776783601L;
	
	public static final String TYPE_NAME = "repository";
	public static final String PLURAL_TYPE_NAME = "repositories";
	
	private String aggregate = null;
	
	// ------------------------------------------------------------------------
	
	public DocumentedRepository(final KasperLibrary kl, final Class<? extends Repository<?>> repositoryClazz) {
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);
		
		// Extract aggregate type from repository -----------------------------
		@SuppressWarnings("unchecked") // Safe
		final Optional<Class<? extends AggregateRoot>> agr =
			(Optional<Class<? extends AggregateRoot>>)
				ReflectionGenericsResolver.getParameterTypeFromClass(
					repositoryClazz, Repository.class, Repository.ENTITY_PARAMETER_POSITION);
		
		if (!agr.isPresent()) {
			throw new KasperException("Unable to find aggregate type for repository " + repositoryClazz.getClass());
		}
		
		// Find associated domain ---------------------------------------------
		final Class<? extends Domain> domain;
		final XKasperConcept conceptAnno = agr.get().getAnnotation(XKasperConcept.class);
		if (null != conceptAnno) {
			domain = conceptAnno.domain();
		} else {
			final XKasperRelation relationAnno = agr.get().getAnnotation(XKasperRelation.class);
			if (null != relationAnno) {
				domain = relationAnno.domain();
			} else {
				throw new KasperException("Unable to find domain from annotation for aggregate " + agr.get().getSimpleName());
			}
		}
		 		
		// Get domain name ----------------------------------------------------
		final XKasperDomain domainAnno = domain.getAnnotation(XKasperDomain.class);
		
		if (null == domainAnno) {
			throw new KasperException("Unable to find a name type for domain " + domain);
		}
		
		// Get description ----------------------------------------------------
		final XKasperRepository annotation = repositoryClazz.getAnnotation(XKasperRepository.class);
		String description = annotation.description();
		if (description.isEmpty()) {
			description = String.format("The repository for %s aggregates", agr.get().getSimpleName());
		}
		
		// Set properties -----------------------------------------------------
		this.setName(repositoryClazz.getSimpleName());
		this.setDomainName(domain.getSimpleName());
		this.setDescription(description);
		this.aggregate = agr.get().getSimpleName();
	}	
	
	// ------------------------------------------------------------------------
	
	public DocumentedNode getAggregate() {
		final KasperLibrary kl = this.getKasperLibrary();
		final Optional<DocumentedEntity> entity = kl.getEntity(getDomainName(), aggregate);
		
		if (entity.isPresent()) {
			return kl.getSimpleNodeFrom( entity.get() ); 
		}
		
		return new DocumentedEntity(getKasperLibrary())
			.setDomainName(getDomainName())
			.setName(this.aggregate)
			.setDescription("[Not resolved]")
			.toSimpleNode();
	}	
	
}
