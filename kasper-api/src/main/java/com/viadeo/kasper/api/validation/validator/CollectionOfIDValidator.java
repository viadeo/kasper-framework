package com.viadeo.kasper.api.validation.validator;

import com.viadeo.kasper.api.ID;
import com.viadeo.kasper.api.validation.AssertID;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;

public class CollectionOfIDValidator implements ConstraintValidator<AssertID, Collection<ID>> {

    private final IDValidator idValidator;

    public CollectionOfIDValidator() {
        this.idValidator = new IDValidator();
    }

    @Override
    public void initialize(AssertID annotation) {
        this.idValidator.initialize(annotation);
    }

    @Override
    public boolean isValid(Collection<ID> values, ConstraintValidatorContext context) {
        if (values == null || values.isEmpty()) {
            return true;
        }
        
        for (ID value : values) {
            if ( ! idValidator.isValid(value, context)) {
                return false;

            }
        }

        return Boolean.TRUE;
    }
}
