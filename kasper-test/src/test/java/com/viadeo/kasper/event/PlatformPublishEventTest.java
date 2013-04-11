package com.viadeo.kasper.event;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.locks.ReentrantLock;

import org.joda.time.DateTime;
import org.junit.Test;

import com.viadeo.kasper.AbstractPlatformTests;
import com.viadeo.kasper.IKasperID;
import com.viadeo.kasper.KasperTestIdGenerator;
import com.viadeo.kasper.ddd.annotation.XKasperDomain;
import com.viadeo.kasper.ddd.impl.AbstractDomain;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.annotation.XKasperEventListener;
import com.viadeo.kasper.event.domain.er.impl.AbstractConceptEvent;
import com.viadeo.kasper.event.impl.AbstractEventListener;

public class PlatformPublishEventTest extends AbstractPlatformTests {

	private static final ReentrantLock lock = new ReentrantLock();
	private static boolean received = false;

	// ------------------------------------------------------------------------

	@XKasperDomain(label = "testDomain", prefix = "tst", description = "test domain")
	public static class TestDomain extends AbstractDomain {}

	@SuppressWarnings("serial")
	@XKasperEvent(domain = TestDomain.class, action = "test")
	public static class TestEvent extends AbstractConceptEvent {
		public TestEvent(final IKasperID id_shortMessage, final DateTime creationDate) {
			super(id_shortMessage, creationDate);
		}
	}

	@XKasperEventListener
	public static class TestListener extends AbstractEventListener<TestEvent> {
		@Override
		public void handle(final IEventMessage<TestEvent> eventMessage) {
			received = true;
			lock.unlock();
		}
	}

	// ------------------------------------------------------------------------

	@Test
	public void test() {
		final IKasperID id = KasperTestIdGenerator.get();
		final IEvent event = new TestEvent(id, new DateTime());
		event.setContext(this.newContext());

		lock.lock();
		this.getPlatform().publishEvent(event);		
		lock.lock();

		assertTrue(received);
		lock.unlock();
	}

}
