// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.interceptor;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.QueryInterceptor;
import com.viadeo.kasper.core.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class QueryFilterInterceptorFactory extends QueryInterceptorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryFilterInterceptorFactory.class);

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> create(final TypeToken<?> type) {
        final XKasperQueryFilter annotation = type.getRawType().getAnnotation(XKasperQueryFilter.class);

        if (null != annotation) {
            final Class<? extends QueryInterceptor>[] classes = annotation.value();
            final List<QueryInterceptor<Query, QueryResult>> interceptors = Lists.newArrayList();

            for (final Class<? extends QueryInterceptor> interceptorClass : classes) {
                try {
                    interceptors.add(interceptorClass.newInstance());
                } catch (final ReflectiveOperationException e) {
                    LOGGER.warn("Unexpected error when instantiating interceptor for `{}`", type.getRawType(), e);
                    return Optional.absent();
                }
            }

            return Optional.of(InterceptorChain.makeChain(interceptors));
        }

        return Optional.absent();
    }

}
