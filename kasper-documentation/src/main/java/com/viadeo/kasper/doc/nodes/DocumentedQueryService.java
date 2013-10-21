// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.viadeo.kasper.core.resolvers.QueryServiceResolver;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryAnswer;
import com.viadeo.kasper.cqrs.query.QueryService;
import com.viadeo.kasper.doc.KasperLibrary;
import com.google.common.base.Optional;

public final class DocumentedQueryService extends DocumentedDomainNode {
	private static final long serialVersionUID = -4593630427564176805L;

	public static final String TYPE_NAME = "queryservice";
	public static final String PLURAL_TYPE_NAME = "queryservices";

	private String queryName;
	private String queryAnswerName;
	

	DocumentedQueryService(final KasperLibrary kl) {
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);
	}

	public DocumentedQueryService(final KasperLibrary kl, final Class<? extends QueryService> queryServiceClazz) {
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);

        final QueryServiceResolver resolver = this.getKasperLibrary().getResolverFactory().getQueryServiceResolver();
		final String label = resolver.getLabel(queryServiceClazz);
		final String description = resolver.getDescription(queryServiceClazz);
        final String domainName = resolver.getDomainClass(queryServiceClazz).get().getSimpleName();

		// - Register the domain to the locator --------------------------------
		this.setName(queryServiceClazz.getSimpleName());
		this.setDescription(description);
		this.setLabel(label);
		this.setDomainName(domainName);

        final QueryServiceResolver queryServiceResolver =
                this.getKasperLibrary().getResolverFactory().getQueryServiceResolver();

		// - the Query --------------------------------------------------------
		@SuppressWarnings("unchecked") // Safe
		final Class<? extends Query> queryClass = queryServiceResolver.getQueryClass(queryServiceClazz);
		this.queryName = queryClass.getSimpleName();
		
		// - the Answer -------------------------------------------------------
		@SuppressWarnings("unchecked") // Safe
		final Class<? extends QueryAnswer> queryAnswerClass = queryServiceResolver.getQueryAnswerClass(queryServiceClazz);
		this.queryAnswerName = queryAnswerClass.getSimpleName();

        this.getKasperLibrary().registerQueryServiceForQuery(this, this.queryName);
        this.getKasperLibrary().registerQueryServiceForQueryAnswer(this, this.queryAnswerName);
	}

	@JsonIgnore
	public String getQueryName(){
		return this.queryName;
	}

	public DocumentedNode getQuery() {
		final KasperLibrary kl = this.getKasperLibrary();
		final Optional<DocumentedQuery> query = kl.getQuery(this.queryName);
		
		if (query.isPresent()) {
			return kl.getSimpleNodeFrom( query.get() ); 
		}
		
		return new DocumentedQuery(getKasperLibrary())
			.setDomainName(getDomainName())
			.setName(this.queryName)
			.setDescription("[Not resolved]")
			.toSimpleNode();
	}
	
	@JsonIgnore
	public String getQueryAnswerName(){
		return this.queryAnswerName;
	}

	public DocumentedNode getQueryAnswer() {
		final KasperLibrary kl = this.getKasperLibrary();
		final Optional<DocumentedQueryAnswer> queryAnswer = kl.getQueryAnswer(this.queryAnswerName);
		
		if (queryAnswer.isPresent()) {
			return kl.getSimpleNodeFrom( queryAnswer.get() ); 
		}
		
		return new DocumentedQueryAnswer(getKasperLibrary())
			.setDomainName(getDomainName())
			.setName(this.queryAnswerName)
			.setDescription("[Not resolved]")
			.toSimpleNode();
	}

}
