// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.doc.KasperLibrary;
import com.viadeo.kasper.exception.KasperException;

import com.viadeo.kasper.core.resolvers.QueryResolver;

import com.google.common.base.Optional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DocumentedQuery extends DocumentedDomainNode{

    private static final long serialVersionUID = -4581629164926662306L;

    public static final String TYPE_NAME="query";
    public static final String PLURAL_TYPE_NAME="queries";

    private DocumentedBean properties = null;

    // ----------------------------------------------------------------------------------

    DocumentedQuery(final KasperLibrary kl){
        super(kl,TYPE_NAME,PLURAL_TYPE_NAME);
    }

    public DocumentedQuery(KasperLibrary kl, final Class<? extends Query> queryClazz) {
        this(kl);
    
        final QueryResolver resolver = this.getKasperLibrary().getResolverFactory().getQueryResolver();

        this.setName(queryClazz.getSimpleName());
        this.setLabel(resolver.getLabel(queryClazz));
        this.setDescription(resolver.getDescription(queryClazz));
        this.properties = new DocumentedBean(queryClazz);
    }

    // ----------------------------------------------------------------------

    public DocumentedNode getQueryService(){
		final KasperLibrary kl = this.getKasperLibrary();
		final Optional<DocumentedQueryService> queryService = kl.getQueryServiceForQuery(getName());

		if (queryService.isPresent()) {
			return kl.getSimpleNodeFrom(queryService.get());
		}

		return null;
    }

    // ----------------------------------------------------------------------

    public DocumentedBean getProperties(){
        return this.properties;
    }

    // ----------------------------------------------------------------------

    public DocumentedNode getDomain(){
        final Optional<DocumentedQueryService> queryService = this.getKasperLibrary().getQueryServiceForQuery(this.getName());

        if (queryService.isPresent()) {
            return queryService.get().getDomain();
        }
        
        return null;
    }

}
