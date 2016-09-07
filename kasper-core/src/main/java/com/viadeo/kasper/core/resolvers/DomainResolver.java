// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
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
import com.viadeo.kasper.api.annotation.XKasperDomain;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.core.component.annotation.XKasperPublic;

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
