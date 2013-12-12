// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.validation;

import com.google.common.base.Optional;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.cqrs.RequestActorsChain;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryRequestActor;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;

import javax.validation.ConstraintViolation;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class QueryValidationActor<Q extends Query, P extends QueryResult> implements QueryRequestActor<Q, P> {

    private final ValidatorFactory validatorFactory;

    // ------------------------------------------------------------------------

    public QueryValidationActor(final ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

    // ------------------------------------------------------------------------

    @Override
    public QueryResponse<P> process(final Q q, final Context context, final RequestActorsChain<Q, QueryResponse<P>> chain) throws Exception {
        final Optional<KasperReason> reason = validate(validatorFactory, q);

        if ( ! reason.isPresent()) {
            return chain.next(q, context);
        } else {
            return QueryResponse.error(reason.get());
        }
    }

    // ------------------------------------------------------------------------

    public static final Optional<KasperReason> validate(final ValidatorFactory validatorFactory, final Object q) {

        final Set<ConstraintViolation<Object>> validations = validatorFactory.getValidator().validate(q);

        if (validations.isEmpty()) {
            return Optional.absent();
        } else {
            final List<String> errors = new ArrayList<>();
            for (final ConstraintViolation<Object> violation : validations) {
                errors.add("VALIDATION:" + violation.getPropertyPath() + ":" + violation.getMessage());
            }
            return Optional.of(new KasperReason(CoreReasonCode.INVALID_INPUT, errors));
        }
    }

}
