package com.viadeo.kasper.event;

import com.viadeo.kasper.AbstractPlatformTests;
import com.viadeo.kasper.IKasperID;
import com.viadeo.kasper.KasperTestIdGenerator;
import com.viadeo.kasper.ddd.annotation.XKasperDomain;
import com.viadeo.kasper.ddd.impl.AbstractDomain;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.annotation.XKasperEventListener;
import com.viadeo.kasper.event.domain.er.impl.AbstractConceptEvent;
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
	public static class TestDomain extends AbstractDomain {}

	@SuppressWarnings("serial")
	@XKasperEvent(domain = TestDomain.class, action = "test")
	public static class TestEvent extends AbstractConceptEvent {
		public TestEvent(final IKasperID idShortMessage, final DateTime creationDate) {
			super(idShortMessage, creationDate);
		}
	}

	@XKasperEventListener
	public static class TestListener extends AbstractEventListener<TestEvent> {
		@Override
		public void handle(final IEventMessage<TestEvent> eventMessage) {
			received = true;
			LOCK.unlock();
		}
	}

	// ------------------------------------------------------------------------

	@Test
	public void test() {
		final IKasperID id = KasperTestIdGenerator.get();
		final IEvent event = new TestEvent(id, new DateTime());
		event.setContext(this.newContext());

		LOCK.lock();
		this.getPlatform().publishEvent(event);		
		LOCK.lock();

		assertTrue(received);
		LOCK.unlock();
	}

}
