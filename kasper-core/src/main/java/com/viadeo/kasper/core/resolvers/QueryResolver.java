// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryService;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQuery;
import com.viadeo.kasper.ddd.Domain;

import static com.google.common.base.Preconditions.checkNotNull;

public class QueryResolver extends AbstractResolver<Query> {

    private QueryServicesLocator queryServicesLocator;
    private QueryServiceResolver queryServiceResolver;

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "Query";
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends Query> clazz) {

        if (cacheDomains.containsKey(clazz)) {
            return Optional.<Class<? extends Domain>>of(cacheDomains.get(clazz));
        }

        final Optional<QueryService> service = this.queryServicesLocator.getServiceFromQueryClass(clazz);

        if (service.isPresent()) {
            final Optional<Class<? extends Domain>> domain =
                    this.queryServiceResolver.getDomainClass(service.get().getClass());

            if (domain.isPresent()) {
                cacheDomains.put(clazz, domain.get());
                return domain;
            }
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    @Override
    public String getDescription(Class<? extends Query> clazz) {
        final XKasperQuery annotation = clazz.getAnnotation(XKasperQuery.class);

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
    public String getLabel(Class<? extends Query> clazz) {
        return clazz.getSimpleName().replace("Query", "");
    }

    // ------------------------------------------------------------------------

    public void setQueryServicesLocator(final QueryServicesLocator queryServicesLocator) {
        this.queryServicesLocator = checkNotNull(queryServicesLocator);
    }

    public void setQueryServiceResolver(final QueryServiceResolver queryServiceResolver) {
        this.queryServiceResolver = checkNotNull(queryServiceResolver);
    }

}
