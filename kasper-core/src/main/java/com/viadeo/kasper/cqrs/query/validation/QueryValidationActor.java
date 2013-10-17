// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.validation;

import com.viadeo.kasper.CoreErrorCode;
import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.cqrs.RequestActorsChain;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryAnswer;
import com.viadeo.kasper.cqrs.query.QueryRequestActor;
import com.viadeo.kasper.cqrs.query.QueryResponse;

import javax.validation.ConstraintViolation;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class QueryValidationActor<Q extends Query, P extends QueryAnswer> implements QueryRequestActor<Q, P> {

    private final ValidatorFactory validatorFactory;

    // ------------------------------------------------------------------------

    public QueryValidationActor(final ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

    // ------------------------------------------------------------------------

    @Override
    public QueryResponse<P> process(final Q q, final Context context, final RequestActorsChain<Q, QueryResponse<P>> chain) throws Exception {
        final QueryResponse<P> queryResponse;

        final Set<ConstraintViolation<Q>> validations = validatorFactory.getValidator().validate(q);
        if (validations.isEmpty()) {
            queryResponse = chain.next(q, context);
        } else {
            final List<String> errors = new ArrayList<>();
            for (final ConstraintViolation<Q> violation : validations) {
                errors.add(violation.getPropertyPath() + " : " + violation.getMessage());
            }
            queryResponse = QueryResponse.of(new KasperError(CoreErrorCode.INVALID_INPUT.name(), errors));
        }

        return queryResponse;
    }

}
