// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.interceptor;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import org.axonframework.commandhandling.interceptors.JSR303ViolationException;

import javax.validation.ConstraintViolation;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class ValidationInterceptor<Q extends Query, R extends QueryResult> implements Interceptor<Q, QueryResponse<R>> {

    private final ValidatorFactory validatorFactory;

    // ------------------------------------------------------------------------

    public ValidationInterceptor(final ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

    // ------------------------------------------------------------------------

    @Override
    public QueryResponse<R> process(final Q q,
                                    final Context context,
                                    final InterceptorChain<Q, QueryResponse<R>> chain) throws Exception {
        validate(validatorFactory, q);
        return chain.next(q, context);
    }

    public static void validate(final ValidatorFactory validatorFactory, final Object obj) {
        final Set<ConstraintViolation<Object>> violations = validatorFactory.getValidator().validate(obj);

        if ( ! violations.isEmpty()) {
            throw new JSR303ViolationException("Validation error on query", violations);
        }
    }

}
