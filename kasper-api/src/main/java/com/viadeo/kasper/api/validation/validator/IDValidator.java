// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.validation.validator;

import com.google.common.collect.Lists;
import com.viadeo.kasper.api.id.ID;
import com.viadeo.kasper.api.validation.AssertID;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IDValidator implements ConstraintValidator<AssertID, ID> {

    private AssertID annotation;

    @Override
    public void initialize(final AssertID annotation) {
        this.annotation = annotation;
    }

    @Override
    public boolean isValid(final ID id, final ConstraintValidatorContext context) {
        if ( id == null ) {
            return Boolean.TRUE;
        }
        return assertVendor(id) && assertObjectType(id) && assertFormat(id);
    }

    protected boolean assertNotNull(final ID id) {
        return id != null;
    }

    protected boolean assertVendor(final ID id) {
        final String expectedVendor = annotation.vendor();
        final String actualVendor = id.getVendor();
        return expectedVendor != null && ( expectedVendor.isEmpty() || expectedVendor.equals(actualVendor) );
    }

    protected boolean assertObjectType(final ID id) {
        final String[] expectedTypes = annotation.type();
        final String actualType = id.getType();
        return expectedTypes != null && ( expectedTypes.length == 0 || Lists.newArrayList(expectedTypes).contains(actualType) );
    }

    protected boolean assertFormat(final ID id) {
        if (annotation.format().isEmpty()) {
            return true;
        }
        final String expectedFormat = annotation.format();
        final String actualFormat = id.getFormat().toString();
        return expectedFormat != null && expectedFormat.equals(actualFormat);
    }
}
