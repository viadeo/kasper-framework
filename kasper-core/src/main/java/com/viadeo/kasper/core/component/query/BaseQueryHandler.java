// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.query;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.exception.KasperCommandException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

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
