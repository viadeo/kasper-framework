// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryService;
import com.viadeo.kasper.ddd.Domain;

import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

public class QueryResolver extends AbstractResolver {

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
    public Optional<Class<? extends Domain>> getDomain(Class clazz) {

        if ( ! Query.class.isAssignableFrom(clazz)) {
            return Optional.absent();
        }

        if (cacheDomains.containsKey(clazz)) {
            return Optional.<Class<? extends Domain>>of(cacheDomains.get(clazz));
        }

        final Optional<QueryService> service = this.queryServicesLocator.getServiceFromQueryClass((Class<? extends Query>) clazz);

        if (service.isPresent()) {
            final Optional<Class<? extends Domain>> domain = this.queryServiceResolver.getDomain(clazz);
            if (domain.isPresent()) {
                cacheDomains.put(clazz, domain.get());
                return domain;
            }
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    public void setQueryServicesLocator(final QueryServicesLocator queryServicesLocator) {
        this.queryServicesLocator = checkNotNull(queryServicesLocator);
    }

    public void setQueryServiceResolver(final QueryServiceResolver queryServiceResolver) {
        this.queryServiceResolver = checkNotNull(queryServiceResolver);
    }

}
