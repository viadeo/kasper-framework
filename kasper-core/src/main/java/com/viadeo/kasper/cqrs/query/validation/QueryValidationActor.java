// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.validation;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.cqrs.RequestActorsChain;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryRequestActor;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import org.axonframework.commandhandling.interceptors.JSR303ViolationException;

import javax.validation.ConstraintViolation;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class QueryValidationActor<Q extends Query, P extends QueryResult> implements QueryRequestActor<Q, P> {

    private final ValidatorFactory validatorFactory;

    // ------------------------------------------------------------------------

    public QueryValidationActor(final ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public QueryResponse<P> process(final Q q, final Context context, final RequestActorsChain<Q, QueryResponse<P>> chain) throws Exception {
        validate(validatorFactory, q);
        return chain.next(q, context);
    }

    public static void validate(ValidatorFactory validatorFactory, Object obj) {
        final Set<ConstraintViolation<Object>> violations = validatorFactory.getValidator().validate(obj);

        if ( ! violations.isEmpty()) {
            throw new JSR303ViolationException("Validation error on query", violations);
        }
    }

}
