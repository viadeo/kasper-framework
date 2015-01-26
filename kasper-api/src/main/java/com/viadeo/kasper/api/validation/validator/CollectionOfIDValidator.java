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
        if (values == null) {
            return true;
        }

        boolean valid = ! values.isEmpty();

        if (valid) {
            for (ID value : values) {
                valid &= idValidator.isValid(value, context);
                if ( ! valid) {
                    break;
                }
            }
        }

        return valid;
    }
}
