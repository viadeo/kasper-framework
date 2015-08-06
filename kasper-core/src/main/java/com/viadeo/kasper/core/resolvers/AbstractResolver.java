// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.annotation.XKasperAlias;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.core.component.annotation.XKasperPublic;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractResolver<T> implements Resolver<T> {

    protected static final ConcurrentMap<Class, Class> DOMAINS_CACHE = Maps.newConcurrentMap();

    protected DomainResolver domainResolver;

    // ------------------------------------------------------------------------

    @Override
    public String getDomainLabel(final Class<? extends T> clazz) {
        final Optional<Class<? extends Domain>> domain = this.getDomainClass(checkNotNull(clazz));

        if (domain.isPresent()) {
            return domainResolver.getLabel(domain.get());
        }

        return "Unknown";
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean isPublic(final Class<? extends T> clazz) {
        return (null != checkNotNull(clazz).getAnnotation(XKasperPublic.class));
    }

    @Override
    public boolean isDeprecated(final Class<? extends T> clazz) {
        return (null != checkNotNull(clazz).getAnnotation(Deprecated.class));
    }

    @Override
    public Optional<List<String>> getAliases(final Class<? extends T> clazz) {
        final XKasperAlias annotation = checkNotNull(clazz).getAnnotation(XKasperAlias.class);

        final List<String> aliases;
        if (null != annotation) {
            aliases = Lists.newArrayList(annotation.values());
        } else {
            aliases = null;
        }

        return Optional.fromNullable(aliases);
    }

    // ------------------------------------------------------------------------

    public void setDomainResolver(final DomainResolver domainResolver) {
        this.domainResolver = checkNotNull(domainResolver);
    }

    // ------------------------------------------------------------------------

    @Override
    public void clearCache() {
        // FIXME: only clear keys related to an assignable class via identification
        // FIXME: of the generic
        DOMAINS_CACHE.clear();
    }

}
