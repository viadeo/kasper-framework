package com.viadeo.kasper.event;

import com.viadeo.kasper.AbstractPlatformTests;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.KasperTestIdGenerator;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.annotation.XKasperDomain;
import com.viadeo.kasper.er.impl.AbstractRootConcept;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.annotation.XKasperEventListener;
import com.viadeo.kasper.event.domain.er.impl.AbstractConceptRootEvent;
import com.viadeo.kasper.event.impl.AbstractEventListener;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.assertTrue;

public class PlatformPublishEventTest extends AbstractPlatformTests {

	private static final ReentrantLock LOCK = new ReentrantLock();
	private static boolean received = false;

	// ------------------------------------------------------------------------

	@XKasperDomain(label = "testDomain", prefix = "tst", description = "test domain")
	public static class TestDomain implements Domain {}

    public static class TestConceptRoot extends AbstractRootConcept {}

	@SuppressWarnings("serial")
	@XKasperEvent(action = "test")
	public static class TestEvent extends AbstractConceptRootEvent<TestDomain, TestConceptRoot> {
		public TestEvent(final KasperID idShortMessage, final DateTime creationDate) {
			super(DefaultContextBuilder.get(), idShortMessage, creationDate);
		}
	}

	@XKasperEventListener( domain = TestDomain.class )
	public static class TestListener extends AbstractEventListener<TestEvent> {
		@Override
		public void handle(final EventMessage<TestEvent> eventMessage) {
			received = true;
			LOCK.unlock();
		}
	}

	// ------------------------------------------------------------------------

	@Test
	public void test() {
		final KasperID id = KasperTestIdGenerator.get();
		final Event event = new TestEvent(id, new DateTime());
		event.setContext(this.newContext());

		LOCK.lock();
		this.getPlatform().publishEvent(event);		
		LOCK.lock();

		assertTrue(received);
		LOCK.unlock();
	}

}
