// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import org.junit.Test;

import javax.validation.constraints.NotNull;
import java.util.Collection;

import static org.junit.Assert.*;

public class DocumentedBeanTest {

    public class ClassWithFieldCollection {
        @SuppressWarnings("unused")
        public Collection<String> fieldCollection;
    }

    @Test
    public void testDetectCollection() {
        // Given
        final DocumentedBean bean = new DocumentedBean(ClassWithFieldCollection.class);

        // Then
        assertEquals(1, bean.size());

        final DocumentedProperty prop = bean.get(0);
        assertEquals("fieldCollection", prop.getName());
        assertEquals("String", prop.getType());
        assertTrue(prop.isList());
    }

    // ------------------------------------------------------------------------

    public class ClassWithFieldCollectionGeneric<E> {
        @SuppressWarnings("unused")
        public Collection<E> fieldCollection;
    }

    public class ClassExtendingCollectionGeneric extends ClassWithFieldCollectionGeneric<String> { }

    @Test
    public void testDetectCollectionGeneric() {
        // Given
        final DocumentedBean bean = new DocumentedBean(ClassExtendingCollectionGeneric.class);

        // Then
        assertEquals(1, bean.size());

        final DocumentedProperty prop = bean.get(0);
        assertEquals("fieldCollection", prop.getName());
        assertEquals("String", prop.getType());
        assertTrue(prop.isList());
    }

    // ------------------------------------------------------------------------

    public class ClassWithBeanValidation {
        @NotNull
        public String iCantBeNull;
        public String imNullable;
    }

    @Test
    public void testDetectBeanValidationAnnotation() {
        // Given
        final DocumentedBean bean = new DocumentedBean(ClassWithBeanValidation.class);

        // Then
        assertEquals(2, bean.size());
        final DocumentedProperty firstProperty = bean.get(0);
        assertEquals("iCantBeNull", firstProperty.getName());
        assertEquals("String", firstProperty.getType());
        assertTrue(firstProperty.isMandatory());

        final DocumentedProperty secondProperty = bean.get(1);
        assertEquals("imNullable", secondProperty.getName());
        assertEquals("String", secondProperty.getType());
        assertFalse(secondProperty.isMandatory());
    }

}
