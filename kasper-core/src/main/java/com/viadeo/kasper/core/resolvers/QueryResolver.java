// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.locators.QueryHandlersLocator;
import com.viadeo.kasper.api.domain.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.api.domain.query.QueryResult;
import com.viadeo.kasper.api.documentation.XKasperQuery;
import com.viadeo.kasper.api.domain.Domain;

import static com.google.common.base.Preconditions.checkNotNull;

public class QueryResolver extends AbstractResolver<Query> {

    private QueryHandlersLocator queryHandlersLocator;
    private QueryHandlerResolver queryHandlerResolver;

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "Query";
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends Query> clazz) {

        if (DOMAINS_CACHE.containsKey(checkNotNull(clazz))) {
            return Optional.<Class<? extends Domain>>of(DOMAINS_CACHE.get(clazz));
        }

        if (null != queryHandlersLocator) {
            final Optional<QueryHandler<Query, QueryResult>> handler =
                    this.queryHandlersLocator.getHandlerFromQueryClass(clazz);

            if (handler.isPresent()) {
                final Optional<Class<? extends Domain>> domain =
                        this.queryHandlerResolver.getDomainClass(handler.get().getClass());

                if (domain.isPresent()) {
                    DOMAINS_CACHE.put(clazz, domain.get());
                    return domain;
                }
            }

        } else {
            return domainResolver.getDomainClassOf(clazz);
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    @Override
    public String getDescription(final Class<? extends Query> clazz) {
        final XKasperQuery annotation =
                checkNotNull(clazz).getAnnotation(XKasperQuery.class);

        String description = "";

        if (null != annotation) {
            description = annotation.description();
        }

        if (description.isEmpty()) {
            description = String.format("The %s query", this.getLabel(clazz));
        }

        return description;
    }

    @Override
    public String getLabel(final Class<? extends Query> clazz) {
        return checkNotNull(clazz).getSimpleName().replace("Query", "");
    }

    // ------------------------------------------------------------------------

    public void setQueryHandlersLocator(final QueryHandlersLocator queryHandlersLocator) {
        this.queryHandlersLocator = checkNotNull(queryHandlersLocator);
    }

    public void setQueryHandlerResolver(final QueryHandlerResolver queryHandlerResolver) {
        this.queryHandlerResolver = checkNotNull(queryHandlerResolver);
    }

}
