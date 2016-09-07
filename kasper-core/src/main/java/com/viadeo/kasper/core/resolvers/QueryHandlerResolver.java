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
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.common.tools.ReflectionGenericsResolver;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;

import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

public class QueryHandlerResolver extends AbstractResolver<QueryHandler> {

    private static final ConcurrentMap<Class, Class> QUERY_CACHE = Maps.newConcurrentMap();
    private static final ConcurrentMap<Class, Class> RESULT_CACHE = Maps.newConcurrentMap();

    // ------------------------------------------------------------------------

    public QueryHandlerResolver() {
        super();
    }

    public QueryHandlerResolver(final DomainResolver domainResolver) {
        this();
        setDomainResolver(checkNotNull(domainResolver));
    }

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "QueryHandler";
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends QueryHandler> clazz) {

        if (DOMAINS_CACHE.containsKey(checkNotNull(clazz))) {
            return Optional.<Class<? extends Domain>>of(DOMAINS_CACHE.get(clazz));
        }

        final XKasperQueryHandler annotation = clazz.getAnnotation(XKasperQueryHandler.class);

        if (null != annotation) {
            final Class<? extends Domain> domain = annotation.domain();
            DOMAINS_CACHE.put(clazz, domain);
            return Optional.<Class<? extends Domain>>of(domain);
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    public String getDescription(Class<? extends QueryHandler> clazz) {
        final XKasperQueryHandler annotation =
                checkNotNull(clazz).getAnnotation(XKasperQueryHandler.class);

        String description = "";

        if (null != annotation) {
            description = annotation.description();
        }

        if (description.isEmpty()) {
            description = String.format("The %s query handler", this.getLabel(clazz));
        }

        return description;
    }

    @Override
    public String getLabel(final Class<? extends QueryHandler> clazz) {
        return checkNotNull(clazz).getSimpleName()
                .replace("QueryHandler", "")
                .replace("Handler", "");
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public Class<? extends Query> getQueryClass(final Class<? extends QueryHandler> clazz) {

        if (QUERY_CACHE.containsKey(checkNotNull(clazz))) {
            return QUERY_CACHE.get(clazz);
        }

        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<? extends Query>> queryClazz =
                (Optional<Class<? extends Query>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                clazz,
                                QueryHandler.class,
                                QueryHandler.PARAMETER_QUERY_POSITION
                        );

        if ( ! queryClazz.isPresent()) {
            throw new KasperException("Unable to find query type for query handler " + clazz.getClass());
        }

        QUERY_CACHE.put(clazz, queryClazz.get());
        return queryClazz.get();
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public Class<? extends QueryResult> getQueryResultClass(final Class<? extends QueryHandler> clazz) {

        if (RESULT_CACHE.containsKey(checkNotNull(clazz))) {
            return RESULT_CACHE.get(clazz);
        }

        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<? extends QueryResult>> queryResultClazz =
                (Optional<Class<? extends QueryResult>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                clazz,
                                QueryHandler.class,
                                QueryHandler.PARAMETER_RESULT_POSITION
                        );

        if ( ! queryResultClazz.isPresent()) {
            throw new KasperException("Unable to find query result type for query handler " + clazz.getClass());
        }

        RESULT_CACHE.put(clazz, queryResultClazz.get());
        return queryResultClazz.get();
    }

}
