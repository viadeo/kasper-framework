// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.google.common.base.Optional;
import com.viadeo.kasper.exception.KasperException;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SimpleComponentsInstanceManagerTest {

    @Test
    public void getInstanceShouldBeDeterministic() {

        // Given
        SimpleComponentsInstanceManager instanceManager = new SimpleComponentsInstanceManager();

        // When
        final Optional<String> optStr1 = instanceManager.getInstanceFromClass(String.class);
        final Optional<String> optStr2 = instanceManager.getInstanceFromClass(String.class);

        // Then
        assertTrue(optStr1.isPresent());
        assertTrue(optStr2.isPresent());
        assertTrue(optStr1.get() == optStr2.get());
    }

    @Test
    public void multipleRecordInstanceCallsMustFail() {

        // Given
        final SimpleComponentsInstanceManager instanceManager = new SimpleComponentsInstanceManager();
        final String instString = "test";

        // When
        instanceManager.recordInstance(String.class, instString);
        final Optional<String> optGetString = instanceManager.getInstanceFromClass(String.class);

        // Then
        assertTrue(optGetString.isPresent());
        assertEquals(optGetString.get(), instString);

        // --

        try {
            // When
            instanceManager.recordInstance(String.class, instString);
            // Then should not
            fail();
        } catch (final KasperException e) {
            // Then OK
        }

        // --

        try {
            // When
            instanceManager.recordInstance(String.class, "test2");
            // Then should not
            fail();
        } catch (final KasperException e) {
            // Then OK
        }

    }

}
