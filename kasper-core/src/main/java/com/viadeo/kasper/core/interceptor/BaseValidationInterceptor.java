// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor;

import org.axonframework.commandhandling.interceptors.JSR303ViolationException;

import javax.validation.ConstraintViolation;
import javax.validation.ValidatorFactory;
import java.util.Set;

public abstract class BaseValidationInterceptor<E> {

    private final ValidatorFactory validatorFactory;

    // ------------------------------------------------------------------------

    public BaseValidationInterceptor(final ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

    // ------------------------------------------------------------------------

    public void validate(final E obj) {
        final Set<ConstraintViolation<Object>> violations = validatorFactory.getValidator().validate((Object) obj);

        if (!violations.isEmpty()) {
            throw new JSR303ViolationException("One or more JSR303 constraints were violated.", violations);
        }
    }
}
