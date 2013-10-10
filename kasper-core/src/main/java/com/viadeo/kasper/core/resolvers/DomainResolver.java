// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.ddd.annotation.XKasperDomain;
import com.viadeo.kasper.event.EventListener;

import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Tool resolver for domain components
 */
public class DomainResolver {

    private static ConcurrentMap<Class, String> cacheDomains = Maps.newConcurrentMap();
    private static ConcurrentMap<Class, String> cacheTypes = Maps.newConcurrentMap();

    private CommandResolver commandResolver;
    private QueryResolver queryResolver;
    private EventListenerResolver eventListenerResolver;
    private RepositoryResolver repositoryResolver;

    // ------------------------------------------------------------------------

    public Optional<String> getDomainLabel(final Class<? extends Domain> clazz) {
        final XKasperDomain domainAnnotation = clazz.getAnnotation(XKasperDomain.class);
        if (null != domainAnnotation) {
            return Optional.of(domainAnnotation.label());
        }
        return Optional.absent();
    }

    // -------------------------------------------------------------------------

    public Optional<String> getDomainLabelFromClass(final Class<?> clazz) {

        if (cacheDomains.containsKey(clazz)) {
            final String domainName = cacheDomains.get(clazz);
            if (domainName.isEmpty()) {
                return Optional.absent();
            } else {
                return Optional.of(domainName);
            }
        }

        Optional<Class<? extends Domain>> domain = null;

        if (Command.class.isAssignableFrom(clazz)) {
            domain = commandResolver.getDomain(clazz);
        }
        if (Query.class.isAssignableFrom(clazz)) {
            domain = queryResolver.getDomain(clazz);
        }
        if (EventListener.class.isAssignableFrom(clazz)) {
            domain = eventListenerResolver.getDomain(clazz);
        }
        if (IRepository.class.isAssignableFrom(clazz)) {
            domain = repositoryResolver.getDomain(clazz);
        }

        String domainLabel = null;

        if (domain.isPresent()) {
            final Optional<String> optDomainLabel = this.getDomainLabel(domain.get());
            if (optDomainLabel.isPresent()) {
                domainLabel = optDomainLabel.get();
            }
        }

        if (null != domainLabel) {
            cacheDomains.put(clazz, domainLabel);
        } else {
            cacheDomains.put(clazz, "");
        }

        return Optional.fromNullable(domainLabel);
    }

    // ------------------------------------------------------------------------

    public Optional<String> getComponentTypeName(final Class<?> clazz) {

        if (cacheTypes.containsKey(clazz)) {
            final String typeName = cacheTypes.get(clazz);
            if (typeName.isEmpty()) {
                return Optional.absent();
            } else {
                return Optional.of(typeName);
            }
        }

        String typeName = null;

        if (Command.class.isAssignableFrom(clazz)) {
            typeName = commandResolver.getTypeName();
        }

        if (Query.class.isAssignableFrom(clazz)) {
            typeName = queryResolver.getTypeName();
        }

        if (EventListener.class.isAssignableFrom(clazz)) {
            typeName = eventListenerResolver.getTypeName();
        }

        if (IRepository.class.isAssignableFrom(clazz)) {
            typeName = repositoryResolver.getTypeName();
        }

        if (null != typeName) {
            cacheTypes.put(clazz, typeName);
        } else {
            cacheTypes.put(clazz, "");
        }

        return Optional.fromNullable(typeName);
    }

    // ------------------------------------------------------------------------

    public void setCommandResolver(final CommandResolver commandResolver) {
        this.commandResolver = checkNotNull(commandResolver);
    }

    public void setQueryResolver(final QueryResolver queryResolver) {
        this.queryResolver = checkNotNull(queryResolver);
    }

    public void setEventListenerResolver(final EventListenerResolver eventListenerResolver) {
        this.eventListenerResolver = checkNotNull(eventListenerResolver);
    }

    public void setRepositoryResolver(final RepositoryResolver repositoryResolver) {
        this.repositoryResolver = checkNotNull(repositoryResolver);
    }

}
