package com.viadeo.kasper.cqrs.query.validation;

import com.viadeo.kasper.CoreErrorCode;
import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.cqrs.RequestActor;
import com.viadeo.kasper.cqrs.RequestActorsChain;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryPayload;
import com.viadeo.kasper.cqrs.query.QueryRequestActor;
import com.viadeo.kasper.cqrs.query.QueryResult;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class QueryValidationActor<Q extends Query, P extends QueryPayload> implements QueryRequestActor<Q, P> {

    private final ValidatorFactory validatorFactory;

    public QueryValidationActor(final ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

    @Override
    public QueryResult<P> process(final Q q, final Context context, final RequestActorsChain<Q, QueryResult<P>> chain) throws Exception {
        final Set<ConstraintViolation<Q>> validations = validatorFactory.getValidator().validate(q);
        if (validations.isEmpty()) return chain.next(q, context);
        else {
            final List<String> errors = new ArrayList<>();
            for (ConstraintViolation<Q> violation : validations) {
                errors.add(violation.getPropertyPath() + " : " + violation.getMessage());
            }
            return QueryResult.of(new KasperError(CoreErrorCode.INVALID_INPUT.name(), errors));
        }
    }
}
