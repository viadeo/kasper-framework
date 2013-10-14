// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.resolvers.RepositoryResolver;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.doc.KasperLibrary;

public final class DocumentedRepository extends DocumentedDomainNode {
	private static final long serialVersionUID = 2245288475776783601L;
	
	public static final String TYPE_NAME = "repository";
	public static final String PLURAL_TYPE_NAME = "repositories";
	
	private String aggregate = null;
	
	// ------------------------------------------------------------------------
	
	public DocumentedRepository(final KasperLibrary kl, final Class<? extends IRepository> repositoryClazz) {
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);

        final RepositoryResolver resolver = this.getKasperLibrary().getResolverFactory().getRepositoryResolver();

		// Extract aggregate type from repository -----------------------------
		@SuppressWarnings("unchecked") // Safe
		final Class<? extends AggregateRoot> agr = resolver.getStoredEntityClass(repositoryClazz);

		// Find associated domain ---------------------------------------------
		final String domainName = resolver.getDomainClass(repositoryClazz).get().getSimpleName();
        final String description = resolver.getDescription(repositoryClazz);

		// Set properties -----------------------------------------------------
		this.setName(repositoryClazz.getSimpleName());
		this.setDomainName(domainName);
		this.setDescription(description);
		this.aggregate = agr.getSimpleName();
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
