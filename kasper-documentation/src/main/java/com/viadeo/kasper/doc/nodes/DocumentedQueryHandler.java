// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import com.viadeo.kasper.core.resolvers.QueryHandlerResolver;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.doc.KasperLibrary;
import com.google.common.base.Optional;

public final class DocumentedQueryHandler extends DocumentedDomainNode {
	private static final long serialVersionUID = -4593630427564176805L;

	public static final String TYPE_NAME = "queryservice";
	public static final String PLURAL_TYPE_NAME = "queryservices";

    private String queryName;
	private String queryResultName;

	DocumentedQueryHandler(final KasperLibrary kl) { // Used as empty command to
												     // populate
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);
	}

	public DocumentedQueryHandler(final KasperLibrary kl, final Class<? extends QueryHandler> queryHandlerClazz) {
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);

        final QueryHandlerResolver resolver = this.getKasperLibrary().getResolverFactory().getQueryHandlerResolver();
		final String label = resolver.getLabel(queryHandlerClazz);
		final String description = resolver.getDescription(queryHandlerClazz);
        final String domainName = resolver.getDomainClass(queryHandlerClazz).get().getSimpleName();

		// - Register the domain to the locator --------------------------------
		this.setName(queryHandlerClazz.getSimpleName());
		this.setDescription(description);
		this.setLabel(label);
		this.setDomainName(domainName);

		// - the Query --------------------------------------------------------
		@SuppressWarnings("unchecked") // Safe
		final Class<? extends Query> queryClass =
                resolver.getQueryClass(queryHandlerClazz);
		this.queryName = queryClass.getSimpleName();
		
		// - the Result -------------------------------------------------------
		final Class<? extends QueryResult> queryResultClass =
                resolver.getQueryResultClass(queryHandlerClazz);
        this.queryResultName = queryResultClass.getSimpleName();
	}

	// ------------------------------------------------------------------------

    @JsonIgnore
	public String getQueryName() {
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
	
    public DocumentedNode getQueryResult(){
        final KasperLibrary kl = this.getKasperLibrary();
        final Optional<DocumentedQueryResult> queryResult=kl.getQueryResult(this.queryResultName);

        if (queryResult.isPresent()){
            return kl.getSimpleNodeFrom(queryResult.get());
        }

        return new DocumentedQueryResult(getKasperLibrary())
                .setDomainName(getDomainName())
                .setName(this.queryResultName)
                .setDescription("[Not resolved]")
                .toSimpleNode();
    }

    @JsonIgnore
    public String getQueryResultName(){
        return this.queryResultName;
    }

}
