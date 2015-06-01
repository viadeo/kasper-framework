// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor;

import com.google.common.base.Joiner;
import org.axonframework.commandhandling.interceptors.JSR303ViolationException;

import javax.validation.ConstraintViolation;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseValidationInterceptor<E> {

    private static final String exceptionMessageStart = "One or more JSR303 constraints were violated. ";

    private final ValidatorFactory validatorFactory;

    // ------------------------------------------------------------------------

    public BaseValidationInterceptor(final ValidatorFactory validatorFactory) {
        this.validatorFactory = checkNotNull(validatorFactory);
    }

    // ------------------------------------------------------------------------

    public void validate(final E obj) {
        checkNotNull(obj);
        final Set<ConstraintViolation<Object>> violations = validatorFactory.getValidator().validate((Object) obj);

        if ( ! violations.isEmpty()) {
            final String exceptionMessage = buildExceptionMessage(violations);
            throw new JSR303ViolationException(exceptionMessage, violations);
        }
    }

    private static String buildExceptionMessage(final Set<ConstraintViolation<Object>> violations) {
        final List<String> violationMessages = new ArrayList<>();
        for (final ConstraintViolation<Object> violation : violations) {
            violationMessages.add("Field " + violation.getPropertyPath() + " = [" + violation.getInvalidValue() + "]: " + violation.getMessage());
        }
        return exceptionMessageStart + Joiner.on(" ; ").join(violationMessages);
    }

}

