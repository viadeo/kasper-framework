// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes.validation;

import com.viadeo.kasper.doc.nodes.DocumentedProperty;

import java.lang.reflect.Field;

public class ValidatedBeanPropertyVisitor {

    private static final ValidatedBeanPropertyVisitor[] processors = new ValidatedBeanPropertyVisitor[] {
        new NotNullVisitor()
    };

    public void process(final Field field, final DocumentedProperty property) {
        for (final ValidatedBeanPropertyVisitor processor : processors) {
            processor.process(field, property);
        }
    }

}
