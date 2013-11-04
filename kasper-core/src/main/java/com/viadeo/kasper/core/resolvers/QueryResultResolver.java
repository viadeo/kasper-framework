// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.locators.QueryHandlersLocator;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryResult;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.exception.KasperException;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

public class QueryResultResolver extends AbstractResolver<QueryResult> {

    private QueryHandlersLocator queryHandlersLocator;
    private QueryHandlerResolver queryHandlerResolver;

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "QueryResult";
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends QueryResult> clazz) {
        if (cacheDomains.containsKey(clazz)) {
            return Optional.<Class<? extends Domain>>of(cacheDomains.get(clazz));
        }

        final Collection<QueryHandler> queryHandlers = this.queryHandlersLocator.getHandlersFromQueryResultClass(clazz);

        Optional<Class<? extends Domain>> result = Optional.absent();
        for (QueryHandler queryHandler : queryHandlers) {
            final Optional<Class<? extends Domain>> domain =
                    this.queryHandlerResolver.getDomainClass(queryHandler.getClass());
            if (domain.isPresent()) {
                if (result.isPresent()) {
                    throw new KasperException("More than one domain found");
                }
                result = domain;
            }
            
        }
        
        if (result.isPresent()) {
            cacheDomains.put(clazz, result.get());
        } 
    
        return result;
    }

    // ------------------------------------------------------------------------

    @Override
    public String getDescription(Class<? extends QueryResult> clazz) {
        final XKasperQueryResult annotation = clazz.getAnnotation(XKasperQueryResult.class);

        String description = "";
        if (null != annotation) {
            description = annotation.description();
        }
        if (description.isEmpty()) {
            description = String.format("The %s query answer", this.getLabel(clazz));
        }

        return description;
    }

    @Override
    public String getLabel(Class<? extends QueryResult> clazz) {
        return clazz.getSimpleName().replace("QueryResult", "");
    }

    // ------------------------------------------------------------------------

    public void setQueryHandlersLocator(final QueryHandlersLocator queryHandlersLocator) {
        this.queryHandlersLocator = checkNotNull(queryHandlersLocator);
    }

    public void setQueryHandlerResolver(final QueryHandlerResolver queryHandlerResolver) {
        this.queryHandlerResolver = checkNotNull(queryHandlerResolver);
    }

}
