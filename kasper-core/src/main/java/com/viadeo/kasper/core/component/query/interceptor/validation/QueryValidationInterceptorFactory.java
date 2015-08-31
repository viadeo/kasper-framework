// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.query.interceptor.validation;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.component.query.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validation;
import javax.validation.ValidationException;

public class QueryValidationInterceptorFactory extends QueryInterceptorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryValidationInterceptorFactory.class);

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> create(final TypeToken<?> type) {
        final Interceptor<Query, QueryResponse<QueryResult>> interceptor;

        try {
            interceptor = new QueryValidationInterceptor(Validation.buildDefaultValidatorFactory());
        } catch (final ValidationException ve) {
            LOGGER.warn(
                    "Unexpected error when instantiating interceptor for `{}` : "
                  + "No implementation found for BEAN VALIDATION - JSR 303",
                    type.getRawType(), ve
            );
            return Optional.absent();
        }

        return Optional.of(InterceptorChain.makeChain(interceptor));
    }

}
