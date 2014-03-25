// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes.validation;

import com.viadeo.kasper.doc.nodes.DocumentedProperty;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import static com.google.common.base.Preconditions.checkNotNull;

public class PropertyValidationProcessor {

    private final PropertyValidator validator;

    public PropertyValidationProcessor(final PropertyValidator validator) {
        this.validator = checkNotNull(validator);
    }

    public void process(final Field field, final DocumentedProperty documentedProperty) {
        for (final Annotation annotation : field.getDeclaredAnnotations()) {
            validator.validate(annotation, documentedProperty);
        }
    }
}
