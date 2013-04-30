// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.google.common.base.Optional;
import com.viadeo.kasper.cqrs.query.IQuery;
import com.viadeo.kasper.cqrs.query.IQueryDTO;
import com.viadeo.kasper.cqrs.query.IQueryService;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;
import com.viadeo.kasper.doc.KasperLibrary;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

public final class DocumentedQueryService extends AbstractDocumentedDomainNode {
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

	public DocumentedQueryService(final KasperLibrary kl, final Class<? extends IQueryService<?,?>> queryServiceClazz) {
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

		// - the Query --------------------------------------------------------
		@SuppressWarnings("unchecked") // Safe
		final Optional<Class<? extends IQuery>> optQueryClass =
				(Optional<Class<? extends IQuery>>)
						ReflectionGenericsResolver.getParameterTypeFromClass(
								queryServiceClazz, IQueryService.class, IQueryService.PARAMETER_QUERY_POSITION);
		this.query = new DocumentedBean(optQueryClass.get());
		
		// - the DTO ----------------------------------------------------------
		@SuppressWarnings("unchecked") // Safe
		final Optional<Class<? extends IQueryDTO>> optQueryDTOClass =
				(Optional<Class<? extends IQueryDTO>>)
						ReflectionGenericsResolver.getParameterTypeFromClass(
								queryServiceClazz, IQueryService.class, IQueryService.PARAMETER_DTO_POSITION);
		this.response = new DocumentedBean(optQueryDTOClass.get());
		
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
