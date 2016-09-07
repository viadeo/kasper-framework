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
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.annotation.XKasperQueryResult;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.query.CollectionQueryResult;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.common.tools.ReflectionGenericsResolver;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.locators.QueryHandlersLocator;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

public class QueryResultResolver extends AbstractResolver<QueryResult> {

    private static ConcurrentMap<Class, Class> cacheElements = Maps.newConcurrentMap();

    private QueryHandlersLocator queryHandlersLocator;
    private QueryHandlerResolver queryHandlerResolver;

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "QueryResult";
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public Class<? extends QueryResult> getElementClass(final Class<? extends CollectionQueryResult> clazz) {

        if (cacheElements.containsKey(clazz)) {
            return cacheElements.get(clazz);
        }

        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<? extends QueryResult>> elementClass =
                (Optional<Class<? extends QueryResult>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                clazz,
                                CollectionQueryResult.class,
                                CollectionQueryResult.PARAMETER_RESULT_POSITION
                        );

        if ( ! elementClass.isPresent()) {
            throw new KasperException("Unable to find command type for handler " + clazz.getClass());
        }

        cacheElements.put(clazz, elementClass.get());

        return elementClass.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends QueryResult> clazz) {

        if (DOMAINS_CACHE.containsKey(checkNotNull(clazz))) {
            return Optional.<Class<? extends Domain>>of(DOMAINS_CACHE.get(clazz));
        }

        Optional<Class<? extends Domain>> result = Optional.absent();

        if (null != queryHandlersLocator) {
            final Collection<QueryHandler> queryHandlers =
                    this.queryHandlersLocator.getHandlersFromQueryResultClass(clazz);

            for (final QueryHandler queryHandler : queryHandlers) {

                final Optional<Class<? extends Domain>> domain =
                        this.queryHandlerResolver.getDomainClass(queryHandler.getClass());

                if (domain.isPresent()) {
                    if (result.isPresent()) {
                        throw new KasperException("More than one domain found");
                    }
                    result = domain;
                }

            }
        } else {
            result = domainResolver.getDomainClassOf(clazz);
        }
        
        if (result.isPresent()) {
            DOMAINS_CACHE.put(clazz, result.get());
        } 
    
        return result;
    }

    // ------------------------------------------------------------------------

    @Override
    public String getDescription(final Class<? extends QueryResult> clazz) {
        final XKasperQueryResult annotation =
                checkNotNull(clazz).getAnnotation(XKasperQueryResult.class);

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
    public String getLabel(final Class<? extends QueryResult> clazz) {
        return checkNotNull(clazz).getSimpleName()
                .replace("Result", "")
                .replace("QueryResult", "");
    }

    // ------------------------------------------------------------------------

    public void setQueryHandlersLocator(final QueryHandlersLocator queryHandlersLocator) {
        this.queryHandlersLocator = checkNotNull(queryHandlersLocator);
    }

    public void setQueryHandlerResolver(final QueryHandlerResolver queryHandlerResolver) {
        this.queryHandlerResolver = checkNotNull(queryHandlerResolver);
    }

}
