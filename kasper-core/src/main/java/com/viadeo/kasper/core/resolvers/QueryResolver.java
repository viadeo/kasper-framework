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
import com.viadeo.kasper.api.annotation.XKasperQuery;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.locators.QueryHandlersLocator;

import static com.google.common.base.Preconditions.checkNotNull;

public class QueryResolver extends AbstractResolver<Query> {

    private QueryHandlersLocator queryHandlersLocator;
    private QueryHandlerResolver queryHandlerResolver;

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "Query";
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends Query> clazz) {

        if (DOMAINS_CACHE.containsKey(checkNotNull(clazz))) {
            return Optional.<Class<? extends Domain>>of(DOMAINS_CACHE.get(clazz));
        }

        if (null != queryHandlersLocator) {
            final Optional<QueryHandler<Query, QueryResult>> handler =
                    this.queryHandlersLocator.getHandlerFromQueryClass(clazz);

            if (handler.isPresent()) {
                final Optional<Class<? extends Domain>> domain =
                        this.queryHandlerResolver.getDomainClass(handler.get().getClass());

                if (domain.isPresent()) {
                    DOMAINS_CACHE.put(clazz, domain.get());
                    return domain;
                }
            }

        } else {
            return domainResolver.getDomainClassOf(clazz);
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    @Override
    public String getDescription(final Class<? extends Query> clazz) {
        final XKasperQuery annotation =
                checkNotNull(clazz).getAnnotation(XKasperQuery.class);

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
    public String getLabel(final Class<? extends Query> clazz) {
        return checkNotNull(clazz).getSimpleName().replace("Query", "");
    }

    // ------------------------------------------------------------------------

    public void setQueryHandlersLocator(final QueryHandlersLocator queryHandlersLocator) {
        this.queryHandlersLocator = checkNotNull(queryHandlersLocator);
    }

    public void setQueryHandlerResolver(final QueryHandlerResolver queryHandlerResolver) {
        this.queryHandlerResolver = checkNotNull(queryHandlerResolver);
    }

}
