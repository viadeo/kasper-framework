// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryPayload;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.QueryService;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;
import com.viadeo.kasper.doc.KasperLibrary;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

public final class DocumentedQueryService extends DocumentedDomainNode {
	private static final long serialVersionUID = -4593630427564176805L;

	public static final String TYPE_NAME = "queryservice";
	public static final String PLURAL_TYPE_NAME = "queryservices";

    private String queryName;
	private String queryPayloadName;
	
	// ------------------------------------------------------------------------

	DocumentedQueryService(final KasperLibrary kl) { // Used as empty command to
												// populate
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);
	}

	public DocumentedQueryService(final KasperLibrary kl, final Class<? extends QueryService<?,?>> queryServiceClazz) {
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);

        // Extract query type from queryService -----------------------------------------
        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<? extends Query>> queryClazz=
                (Optional<Class<? extends Query>>)
                    ReflectionGenericsResolver.getParameterTypeFromClass(
                        queryServiceClazz, QueryService.class, QueryService.PARAMETER_QUERY_POSITION);

        if (!queryClazz.isPresent()){
            throw new KasperException("Unable to find query type for queryService" + queryServiceClazz.getClass());
        }

        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<? extends QueryPayload>> queryPayloadClazz=
                (Optional<Class<? extends QueryPayload>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                queryServiceClazz, QueryService.class,QueryService.PARAMETER_RESULT_POSITION);

        if (!queryPayloadClazz.isPresent()){
            throw new KasperException("Unable to find queryPayload type for queryService" + queryServiceClazz.getClasses());
        }

        final XKasperQueryService annotation = queryServiceClazz.getAnnotation(XKasperQueryService.class);
        // Find associated domain ----------------------------------------
        final String domainName= annotation.domain().getSimpleName();

        // Get label -----------------------------------------------------
		String label = annotation.name();
		if (label.isEmpty()) {
			label = queryServiceClazz.getSimpleName().replaceAll("QueryService", "");
		}

		// Get description -----------------------------------------------------------
		final String description = String.format("The %s query service", label);

		// - Register the domain to the locator --------------------------------
		this.setName(queryServiceClazz.getSimpleName());
		this.setDescription(description);
		this.setLabel(label);
		this.setDomainName(domainName);

        this.queryName=queryClazz.get().getSimpleName();
		this.queryPayloadName=queryPayloadClazz.get().getSimpleName();
	}

	// ------------------------------------------------------------------------

	public DocumentedNode getQuery() {
		final KasperLibrary kl=this.getKasperLibrary();
        final Optional<DocumentedQuery> query=kl.getQuery(this.queryName);

        if (query.isPresent()){
            return kl.getSimpleNodeFrom(query.get());
        }

        return new DocumentedQuery(getKasperLibrary())
                .setDomainName(getDomainName())
                .setName(this.queryName)
                .setDescription("[Not resolved]")
                .toSimpleNode();
	}
    @JsonIgnore
    public String getQueryName(){
        return this.queryName;
    }

	// ------------------------------------------------------------------------

    public DocumentedNode getQueryPayload(){
        final KasperLibrary kl=this.getKasperLibrary();
        final Optional<DocumentedQueryPayload> queryPayload=kl.getQueryPayload(this.queryPayloadName);

        if (queryPayload.isPresent()){
            return kl.getSimpleNodeFrom(queryPayload.get());
        }

        return new DocumentedQueryPayload(getKasperLibrary())
                .setDomainName(getDomainName())
                .setName(this.queryPayloadName)
                .setDescription("[Not resolved]")
                .toSimpleNode();
    }
    @JsonIgnore
    public String getQueryPayloadName(){
        return this.queryPayloadName;
    }

}
