// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes.validation;

import com.google.common.collect.Sets;
import com.viadeo.kasper.doc.nodes.DocumentedConstraint;
import com.viadeo.kasper.doc.nodes.DocumentedProperty;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;

import static junit.framework.Assert.assertEquals;

public class PropertyValidationProcessorUTest {

    private PropertyValidationProcessor processor;

    // ------------------------------------------------------------------------

    @Before
    public void setUp(){
        processor = new PropertyValidationProcessor(new DefaultPropertyValidator());
    }

    // ------------------------------------------------------------------------

    @Test
    public void process_onField_annotatedByAnyJSR303Annotations_indicateProposal() throws NoSuchFieldException {
        // Given nothing
        final Field field = ObjectWithConstraints.class.getField("fieldAnnotatedByNotNull");
        final DocumentedProperty documentedProperty = new DocumentedProperty(
                field.getName(), field.getType().getSimpleName(), false, Sets.<DocumentedConstraint>newHashSet()
        );

        // When
        processor.process(field, documentedProperty);

        // Then
        assertEquals(1, documentedProperty.getConstraints().size());
        assertEquals("must not be null", documentedProperty.getConstraints().iterator().next().getMessage());
    }

    @Test
    public void process_onField_annotatedByTransient_indicateNoProposal() throws NoSuchFieldException {
        // Given nothing
        final Field field = ObjectWithConstraints.class.getField("fieldAnnotatedByTransient");
        final DocumentedProperty documentedProperty = new DocumentedProperty(
                field.getName(), field.getType().getSimpleName(), false, Sets.<DocumentedConstraint>newHashSet()
        );

        // When
        processor.process(field, documentedProperty);

        // Then
        assertEquals(0, documentedProperty.getConstraints().size());
    }

    @Test
    public void process_onField_unannotated_indicateNoProposal() throws NoSuchFieldException {
        // Given nothing
        final Field field = ObjectWithConstraints.class.getField("unannotatedField");
        final DocumentedProperty documentedProperty = new DocumentedProperty(
                field.getName(), field.getType().getSimpleName(), false, Sets.<DocumentedConstraint>newHashSet()
        );

        // When
        processor.process(field, documentedProperty);

        // Then
        assertEquals(0, documentedProperty.getConstraints().size());
    }

    // ------------------------------------------------------------------------

    public static class ObjectWithConstraints {
        @NotNull
        public String fieldAnnotatedByNotNull;

        public String unannotatedField;

        @Transient
        public String fieldAnnotatedByTransient;
    }

    public static class ObjectWithoutConstraints {
        public String field;
    }

}
