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
