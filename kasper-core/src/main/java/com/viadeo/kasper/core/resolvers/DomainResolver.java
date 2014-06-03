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
import com.viadeo.kasper.annotation.XKasperAlias;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.annotation.XKasperDomain;
import com.viadeo.kasper.security.annotation.XKasperPublic;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Tool resolver for domain components
 */
public class DomainResolver implements Resolver<Domain> {

    private static ConcurrentMap<Class, String> cacheDomains = Maps.newConcurrentMap();

    private DomainHelper domainHelper;

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "Domain";
    }

    // ------------------------------------------------------------------------

    @Override
    public String getLabel(final Class<? extends Domain> clazz) {
        if (cacheDomains.containsKey(checkNotNull(clazz))) {
            return cacheDomains.get(clazz);
        }

        String domainName = clazz.getSimpleName().replace("Domain", "");

        final XKasperDomain domainAnnotation = clazz.getAnnotation(XKasperDomain.class);
        if ((null != domainAnnotation) && ( ! domainAnnotation.label().isEmpty())) {
            domainName = domainAnnotation.label().replaceAll(" ", "");
        }

        if (domainName.isEmpty()) {
            domainName = clazz.getSimpleName().replace("Domain", "");
        }

        domainName = domainName.replaceAll(" ", "");

        cacheDomains.put(clazz, domainName);
        return domainName;
    }

    // ------------------------------------------------------------------------

    @Override
    public String getDescription(final Class<? extends Domain> clazz) {
        String description = "";

        final XKasperDomain domainAnnotation =
                checkNotNull(clazz).getAnnotation(XKasperDomain.class);

        if ((null != domainAnnotation) && ( ! domainAnnotation.description().isEmpty())) {
            description = domainAnnotation.description();
        }

        if (description.isEmpty()) {
            description = String.format("The %s domain", this.getLabel(clazz));
        }

        return description;
    }

    public String getPrefix(final Class<? extends Domain> clazz) {
        String prefix = "";

        final XKasperDomain domainAnnotation =
                checkNotNull(clazz).getAnnotation(XKasperDomain.class);

        if ((null != domainAnnotation) && ( ! domainAnnotation.prefix().isEmpty())) {
            prefix = domainAnnotation.prefix();
        }

        if (prefix.isEmpty()) {
            prefix = "unk";
        }

        return prefix;
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends Domain> clazz) {
        return Optional.<Class<? extends Domain>>of(checkNotNull(clazz));
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClassOf(final Class<?> clazz) {
        if (Domain.class.isAssignableFrom(checkNotNull(clazz))) {
            return getDomainClass((Class<? extends Domain>) clazz);
        } else {
            if (null != domainHelper) {
                return Optional.<Class<? extends Domain>>fromNullable(domainHelper.getDomainClassOf(clazz));
            }
        }
        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public String getDomainLabel(final Class<? extends Domain> clazz) {
        return this.getLabel(checkNotNull(clazz));
    }

    // ------------------------------------------------------------------------


    public void setDomainHelper(final DomainHelper domainHelper) {
        this.domainHelper = checkNotNull(domainHelper);
    }

    // ------------------------------------------------------------------------

    @Override
    public void clearCache() {
        cacheDomains.clear();
    }

    public String getDomainOwner(Class<? extends Domain> clazz) {
        final String owner;

        final XKasperDomain domainAnnotation = checkNotNull(clazz).getAnnotation(XKasperDomain.class);

        if ((null != domainAnnotation) && ( ! domainAnnotation.owner().isEmpty())) {
            owner = domainAnnotation.owner();
        } else {
            owner = "unknown";
        }

        return owner;
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean isPublic(final Class<? extends Domain> clazz) {
        return (null != checkNotNull(clazz).getAnnotation(XKasperPublic.class));
    }

    @Override
    public boolean isDeprecated(final Class<? extends Domain> clazz) {
        return (null != checkNotNull(clazz).getAnnotation(Deprecated.class));
    }

    @Override
    public Optional<List<String>> getAliases(final Class<? extends Domain> clazz) {
        final XKasperAlias annotation = checkNotNull(clazz).getAnnotation(XKasperAlias.class);

        final List<String> aliases;
        if (null != annotation) {
            aliases = Lists.newArrayList(annotation.values());
        } else {
            aliases = null;
        }

        return Optional.fromNullable(aliases);
    }

}
