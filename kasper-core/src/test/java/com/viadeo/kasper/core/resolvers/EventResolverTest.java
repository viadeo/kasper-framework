// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.event.IEvent;
import com.viadeo.kasper.event.domain.DomainEvent;
import com.viadeo.kasper.exception.KasperException;
import org.junit.Test;

import static org.junit.Assert.*;

public class EventResolverTest {

    @XKasperUnregistered
    private static class TestDomain implements Domain {}

    @XKasperUnregistered
    private static class TestEvent implements IEvent { }

    @XKasperUnregistered
    private static class TestDomainEvent implements DomainEvent<TestDomain> { }

    @XKasperUnregistered
    private static class TestGenericDomainEvent implements DomainEvent { }

    // ------------------------------------------------------------------------

    @Test
    public void testGetDomainFromDomainEvent() {
        // Given
        final EventResolver resolver = new EventResolver();

        // When
        final Optional<Class<? extends Domain>> domain =
                resolver.getDomainClass(TestDomainEvent.class);

        // Then
        assertTrue(domain.isPresent());
        assertEquals(TestDomain.class, domain.get());
    }

    @Test
    public void testGetDomainFromGenericDomainEvent() {
        // Given
        final EventResolver resolver = new EventResolver();

        // When
        try {
            final Optional<Class<? extends Domain>> domain =
                    resolver.getDomainClass(TestGenericDomainEvent.class);
            fail();
        } catch (final KasperException e) {
            // Then should raise exception
        }
    }

    @Test
    public void testGetDomainFromEvent() {
        // Given
        final EventResolver resolver = new EventResolver();

        // When
        final Optional<Class<? extends Domain>> domain =
                resolver.getDomainClass(TestEvent.class);

        // Then
        assertFalse(domain.isPresent());
    }

}
