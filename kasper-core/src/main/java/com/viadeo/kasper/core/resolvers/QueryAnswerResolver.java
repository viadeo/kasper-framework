// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.cqrs.query.QueryAnswer;
import com.viadeo.kasper.cqrs.query.QueryService;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryAnswer;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.exception.KasperException;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

public class QueryAnswerResolver extends AbstractResolver<QueryAnswer> {

    private QueryServicesLocator queryServicesLocator;
    private QueryServiceResolver queryServiceResolver;

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "QueryAnswer";
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends QueryAnswer> clazz) {
        if (cacheDomains.containsKey(clazz)) {
            return Optional.<Class<? extends Domain>>of(cacheDomains.get(clazz));
        }

        final Collection<QueryService> queryServices = this.queryServicesLocator.getServicesFromQueryAnswerClass(clazz);

        Optional<Class<? extends Domain>> result = Optional.absent();
        for (QueryService queryService : queryServices) {
            final Optional<Class<? extends Domain>> domain =
                    this.queryServiceResolver.getDomainClass(queryService.getClass());
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
    public String getDescription(Class<? extends QueryAnswer> clazz) {
        final XKasperQueryAnswer annotation = clazz.getAnnotation(XKasperQueryAnswer.class);

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
    public String getLabel(Class<? extends QueryAnswer> clazz) {
        return clazz.getSimpleName().replace("QueryAnswer", "");
    }

    // ------------------------------------------------------------------------

    public void setQueryServicesLocator(final QueryServicesLocator queryServicesLocator) {
        this.queryServicesLocator = checkNotNull(queryServicesLocator);
    }

    public void setQueryServiceResolver(final QueryServiceResolver queryServiceResolver) {
        this.queryServiceResolver = checkNotNull(queryServiceResolver);
    }

}
