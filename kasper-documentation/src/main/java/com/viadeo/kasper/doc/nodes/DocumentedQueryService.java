// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.viadeo.kasper.core.resolvers.QueryServiceResolver;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryPayload;
import com.viadeo.kasper.cqrs.query.QueryService;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;
import com.viadeo.kasper.doc.KasperLibrary;

public final class DocumentedQueryService extends DocumentedDomainNode {
	private static final long serialVersionUID = -4593630427564176805L;

	public static final String TYPE_NAME = "queryservice";
	public static final String PLURAL_TYPE_NAME = "queryservices";

	private DocumentedBean query = null;
	private DocumentedBean response = null;
	
	// ------------------------------------------------------------------------

	DocumentedQueryService(final KasperLibrary kl) { // Used as empty command to
												// populate
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);
	}

	public DocumentedQueryService(final KasperLibrary kl, final Class<? extends QueryService> queryServiceClazz) {
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);

		final XKasperQueryService annotation = queryServiceClazz.getAnnotation(XKasperQueryService.class);
		
		String label = annotation.name();
		if (label.isEmpty()) {
			label = queryServiceClazz.getSimpleName().replaceAll("QueryService", "");
		}
		
		// Get name -----------------------------------------------------------
		final String description = String.format("The %s query service", label);

		// - Register the domain to the locator --------------------------------
		this.setName(queryServiceClazz.getSimpleName());
		this.setDescription(description);
		this.setLabel(label);
		this.setDomainName(annotation.domain().getSimpleName());

        final QueryServiceResolver queryServiceResolver =
                this.getKasperLibrary().getResolverFactory().getQueryServiceResolver();

		// - the Query --------------------------------------------------------
		@SuppressWarnings("unchecked") // Safe
		final Class<? extends Query> optQueryClass =
                queryServiceResolver.getQueryClass(queryServiceClazz);
		this.query = new DocumentedBean(optQueryClass);
		
		// - the Result -------------------------------------------------------
		@SuppressWarnings("unchecked") // Safe
		final Class<? extends QueryPayload> optQueryResultClass =
                queryServiceResolver.getQueryPayloadClass(queryServiceClazz);

		this.response = new DocumentedBean(optQueryResultClass);
	}

	// ------------------------------------------------------------------------

	public DocumentedBean getQuery() {
		return this.query;
	}

	// ------------------------------------------------------------------------

	public DocumentedBean getResponse() {
		return this.response;
	}	

}
