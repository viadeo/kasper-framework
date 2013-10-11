// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.annotation.XKasperDomain;

import java.util.concurrent.ConcurrentMap;

/**
 * Tool resolver for domain components
 */
public class DomainResolver implements Resolver {

    private static ConcurrentMap<Class, String> cacheDomains = Maps.newConcurrentMap();

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "Domain";
    }

    // ------------------------------------------------------------------------

    public Optional<String> getLabel(final Class<? extends Domain> clazz) {
        if (cacheDomains.containsKey(clazz)) {
            return Optional.of(cacheDomains.get(clazz));
        }

        String domainName = null;

        final XKasperDomain domainAnnotation = clazz.getAnnotation(XKasperDomain.class);
        if ((null != domainAnnotation) && ! domainAnnotation.label().isEmpty()) {
            domainName = domainAnnotation.label();
        }

        if (null == domainName) {
            domainName = clazz.getSimpleName().replace("Domain", "");
        }

        domainName = domainName.replaceAll(" ", "");

        cacheDomains.put(clazz, domainName);

        return Optional.of(domainName);
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomain(Class clazz) {
        if (Domain.class.isAssignableFrom(clazz)) {
            return Optional.<Class<? extends Domain>>of(clazz);
        }
        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<String> getDomainLabel(Class<?> clazz) {
        if (Domain.class.isAssignableFrom(clazz)) {
            return this.getLabel((Class<? extends Domain>) clazz);
        }
        return Optional.absent();
    }

}
