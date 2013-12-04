// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes.validation;

import com.viadeo.kasper.doc.nodes.DocumentedProperty;

import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class NotNullProcessor extends ValidatedBeanPropertyProcessor {

    public void process(final Field field, final DocumentedProperty property) {
        final Annotation notNullAnnotation = field.getAnnotation(NotNull.class);
        if (null != notNullAnnotation) {
            property.setMandatory(true);
        }
    }

}
