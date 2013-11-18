// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event;

import com.viadeo.kasper.AbstractPlatformTests;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.KasperTestIdGenerator;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.annotation.XKasperDomain;
import com.viadeo.kasper.er.Concept;
import com.viadeo.kasper.er.annotation.XKasperConcept;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.annotation.XKasperEventListener;
import com.viadeo.kasper.event.domain.EntityCreatedEvent;
import org.junit.Test;

import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.assertTrue;

public class PlatformPublishEventTest extends AbstractPlatformTests {

	private static final ReentrantLock LOCK = new ReentrantLock();
	private static boolean received = false;

	// ------------------------------------------------------------------------

	@XKasperDomain(label = "testDomain", prefix = "tst", description = "test domain")
	public static class TestDomain implements Domain {}

    @XKasperConcept(label = "test root concept", domain = TestDomain.class)
    public static class TestRootConcept extends Concept {}

	@SuppressWarnings("serial")
	@XKasperEvent(action = "test")
	public static class TestEvent extends EntityCreatedEvent<TestDomain> {
		public TestEvent(final KasperID idShortMessage) {
			super(idShortMessage);
		}
	}

	@XKasperEventListener( domain = TestDomain.class )
	public static class TestListener extends EventListener<TestEvent> {
		@Override
		public void handle(final EventMessage<TestEvent> eventMessage) {
			received = true;
		}
	}

	// ------------------------------------------------------------------------

	@Test
	public void testPublishEvent() throws InterruptedException {
        // Given
		final KasperID id = KasperTestIdGenerator.get();
		final Event event = new TestEvent(id);

        // When
		this.getPlatform().publishEvent(event);
        Thread.sleep(3000);

        // Then
		assertTrue(received);
	}

}
