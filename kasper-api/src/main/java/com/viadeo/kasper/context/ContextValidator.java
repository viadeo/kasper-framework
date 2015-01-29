package com.viadeo.kasper.context;

public interface ContextValidator<C extends ImmutableContext> {

    /**
     * Validate the context
     *
     * @param context the context
     * @throws ContextValidationException if there is a validation error
     */
    void validate(C context) throws ContextValidationException;

}
