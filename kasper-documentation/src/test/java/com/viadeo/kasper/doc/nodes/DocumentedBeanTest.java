// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

}
