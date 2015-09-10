// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.common.tools.ReflectionGenericsResolver;
import com.viadeo.kasper.core.component.annotation.XKasperRepository;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;
import com.viadeo.kasper.core.component.command.aggregate.ddd.IRepository;

import static com.google.common.base.Preconditions.checkNotNull;

public class RepositoryResolver extends AbstractResolver<IRepository> {

    private EntityResolver entityResolver;

    // ------------------------------------------------------------------------

    public RepositoryResolver() { }

    public RepositoryResolver(final EntityResolver entityResolver) {
        this.entityResolver = checkNotNull(entityResolver);
    }

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "Repository";
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends IRepository> clazz) {

        if (DOMAINS_CACHE.containsKey(checkNotNull(clazz))) {
            return Optional.<Class<? extends Domain>>of(DOMAINS_CACHE.get(clazz));
        }

        final Class<? extends AggregateRoot> agr = this.getStoredEntityClass(clazz);
        final Optional<Class<? extends Domain>> domain = this.entityResolver.getDomainClass(agr);

        if (domain.isPresent()) {
            DOMAINS_CACHE.put(clazz, domain.get());
            return domain;
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    @Override
    public String getDescription(final Class<? extends IRepository> clazz) {
        final XKasperRepository annotation =
                checkNotNull(clazz).getAnnotation(XKasperRepository.class);

        String description = "";

        if (null != annotation) {
            description = annotation.description();
        }

        if (description.isEmpty()) {
            description = String.format(
                    "The repository for %s aggregates",
                    entityResolver.getLabel(this.getStoredEntityClass(clazz))
            );
        }

        return description;
    }

    @Override
    public String getLabel(final Class<? extends IRepository> clazz) {
        return checkNotNull(clazz).getSimpleName().replace("Repository", "");
    }

    // ------------------------------------------------------------------------

    public Class<? extends AggregateRoot> getStoredEntityClass(final Class<? extends IRepository> clazz) {
        @SuppressWarnings("unchecked")
        final Optional<Class<? extends AggregateRoot>> agr =
                (Optional<Class<? extends AggregateRoot>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                clazz,
                                IRepository.class,
                                IRepository.ENTITY_PARAMETER_POSITION
                        );

        if ( ! agr.isPresent()) {
            throw new KasperException("Unable to find aggregate type for repository " + clazz.getClass());
        }

        return agr.get();
    }

    // ------------------------------------------------------------------------

    public void setEntityResolver(final EntityResolver entityResolver) {
        this.entityResolver = checkNotNull(entityResolver);
    }

}
