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
package com.viadeo.kasper.core.component.query;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.exception.KasperCommandException;
import com.viadeo.kasper.common.tools.ReflectionGenericsResolver;

public abstract class BaseQueryHandler<QUERY extends Query, RESULT extends QueryResult> implements QueryHandler<QUERY,RESULT> {

    private final Class<QUERY> queryClass;
    private final Class<RESULT> resultClass;

    public BaseQueryHandler() {
        @SuppressWarnings("unchecked")
        final Optional<Class<QUERY>> optionalQueryClass =
                (Optional<Class<QUERY>>) (ReflectionGenericsResolver.getParameterTypeFromClass(
                        this.getClass(),
                        BaseQueryHandler.class,
                        BaseQueryHandler.PARAMETER_QUERY_POSITION)
                );

        if ( ! optionalQueryClass.isPresent()) {
            throw new KasperCommandException(
                    "Unable to determine Query class for "
                            + this.getClass().getSimpleName()
            );
        }

        this.queryClass = optionalQueryClass.get();

        @SuppressWarnings("unchecked")
        final Optional<Class<RESULT>> optionalResultClass =
                (Optional<Class<RESULT>>) (ReflectionGenericsResolver.getParameterTypeFromClass(
                        this.getClass(),
                        BaseQueryHandler.class,
                        BaseQueryHandler.PARAMETER_RESULT_POSITION)
                );

        if ( ! optionalQueryClass.isPresent()) {
            throw new KasperCommandException(
                    "Unable to determine QueryResult class for "
                            + this.getClass().getSimpleName()
            );
        }

        this.resultClass = optionalResultClass.get();
    }

    @Override
    public QueryResponse<RESULT> handle(QueryMessage<QUERY> message) {
        return this.handle(message.getContext(), message.getInput());
    }

    /**
     * Handle the <code>Query</code> with his <code>Context</code>.
     *
     * @param context the context related to the request
     * @param query the query requested
     * @return a response
     */
    public QueryResponse<RESULT> handle(Context context, QUERY query) {
        throw new UnsupportedOperationException("not yet implemented!");
    }

    @Override
    public Class<QUERY> getInputClass() {
        return queryClass;
    }

    @Override
    public Class<RESULT> getResultClass() {
        return resultClass;
    }

    @Override
    public Class<? extends QueryHandler> getHandlerClass() {
        return this.getClass();
    }
}
