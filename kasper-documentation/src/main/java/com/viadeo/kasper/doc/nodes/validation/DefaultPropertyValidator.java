// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes.validation;

import com.viadeo.kasper.doc.nodes.DocumentedProperty;

import javax.validation.constraints.*;
import java.lang.annotation.Annotation;

public class DefaultPropertyValidator implements PropertyValidator {

    @Override
    public void validate(final NotNull annotation, final DocumentedProperty property) {
        property.setMandatory(true);
        property.appendConstraint("NotNull", "must not be null");
    }

    @Override
    public void validate(final Null annotation, final DocumentedProperty property) {
        property.appendConstraint("Null", "must be null");
    }

    @Override
    public void validate(final Min annotation, final DocumentedProperty property) {
        property.appendConstraint("Min", "must be higher or equal to " + annotation.value());
    }

    @Override
    public void validate(final Max annotation, final DocumentedProperty property) {
        property.appendConstraint("Max", "must be lower or equal to " + annotation.value());
    }

    @Override
    public void validate(final Size annotation, final DocumentedProperty property) {
        property.appendConstraint("Size", "must be between " + annotation.min() + " and " + annotation.max());
    }

    @Override
    public void validate(final Past annotation, final DocumentedProperty property) {
        property.appendConstraint("Past", "must be a date in the past");
    }

    @Override
    public void validate(final Future annotation, final DocumentedProperty property) {
        property.appendConstraint("Future", "must be a date in the future");
    }

    @Override
    public void validate(final Pattern annotation, final DocumentedProperty property) {
        property.appendConstraint("Pattern", "must match `" + annotation.regexp() + "`");
    }

    @Override
    public void validate(final AssertFalse annotation, final DocumentedProperty property) {
        property.appendConstraint("AssertFalse", "must be false");
    }

    @Override
    public void validate(final AssertTrue annotation, final DocumentedProperty property) {
        property.appendConstraint("AssertTrue", "must be true");
    }

    @Override
    public void validate(final DecimalMax annotation, final DocumentedProperty property) {
        if (annotation.inclusive()) {
            property.appendConstraint("DecimalMax", "must be lower or equal to " + annotation.value());
        } else {
            property.appendConstraint("DecimalMax", "must be lower to " + annotation.value());
        }
    }

    @Override
    public void validate(final DecimalMin annotation, final DocumentedProperty property) {
        if (annotation.inclusive()) {
            property.appendConstraint("DecimalMin", "must be higher or equal to " + annotation.value());
        } else {
            property.appendConstraint("DecimalMin", "must be higher to " + annotation.value());
        }
    }

    @Override
    public void validate(final Digits annotation, final DocumentedProperty property) {
        property.appendConstraint("Digits",
                "must be a number within accepted range (f:" + annotation.fraction() +
                        ", i:" + annotation.integer() + ")"
        );
    }

    @Override
    public void validate(Annotation annotation, DocumentedProperty property) {
        if (NotNull.class.isAssignableFrom(annotation.getClass())) {
            validate((NotNull) annotation, property);
        } else if (Null.class.isAssignableFrom(annotation.getClass())) {
            validate((Null) annotation, property);
        } else if (Min.class.isAssignableFrom(annotation.getClass())) {
            validate((Min) annotation, property);
        } else if (Max.class.isAssignableFrom(annotation.getClass())) {
            validate((Max) annotation, property);
        } else if (Size.class.isAssignableFrom(annotation.getClass())) {
            validate((Size) annotation, property);
        } else if (Past.class.isAssignableFrom(annotation.getClass())) {
            validate((Past) annotation, property);
        } else if (Future.class.isAssignableFrom(annotation.getClass())) {
            validate((Future) annotation, property);
        } else if (Pattern.class.isAssignableFrom(annotation.getClass())) {
            validate((Pattern) annotation, property);
        } else if (AssertFalse.class.isAssignableFrom(annotation.getClass())) {
            validate((AssertFalse) annotation, property);
        } else if (AssertTrue.class.isAssignableFrom(annotation.getClass())) {
            validate((AssertTrue) annotation, property);
        } else if (DecimalMax.class.isAssignableFrom(annotation.getClass())) {
            validate((DecimalMax) annotation, property);
        } else if (DecimalMin.class.isAssignableFrom(annotation.getClass())) {
            validate((DecimalMin) annotation, property);
        } else if (Digits.class.isAssignableFrom(annotation.getClass())) {
            validate((Digits) annotation, property);
        }
    }
}
