// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.impl;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.domain.impl.AbstractEntityCreatedEvent;
import com.viadeo.kasper.event.domain.impl.AbstractEntityUpdatedEvent;
import com.viadeo.kasper.impl.DefaultKasperId;
import org.axonframework.eventhandling.SimpleEventBus;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventstore.EventStore;
import org.axonframework.unitofwork.CurrentUnitOfWork;
import org.axonframework.unitofwork.DefaultUnitOfWork;
import org.joda.time.DateTime;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class RepositoryMarksEventsTest {

    @XKasperUnregistered
    private static class TestAggregate extends AbstractAggregateRoot {

        public CreatedEvent createdEvent;
        public UpdatedEvent updatedEvent;

        @XKasperUnregistered
        static class CreatedEvent extends AbstractEntityCreatedEvent {
            protected CreatedEvent(Context context, KasperID id, DateTime lastModificationDate) {
                super(context, id, lastModificationDate);
            }
        }

        @XKasperUnregistered
        static class UpdatedEvent extends AbstractEntityUpdatedEvent {
            protected UpdatedEvent(Context context, KasperID id, Long version, DateTime lastModificationDate) {
                super(context, id, version, lastModificationDate);
            }
        }

        private TestAggregate() { /* event-sourcing */ }

        TestAggregate(Context context, KasperID id) {
            createdEvent = new CreatedEvent(context, id, DateTime.now());
            apply(createdEvent);
        }

        @EventHandler
        void onCreated(CreatedEvent event) {
            setId(event.getEntityId());
        }

        void makeSomeModification(Context context) {
            updatedEvent = new UpdatedEvent(context, this.getEntityId(), this.getVersion(), DateTime.now());
            apply(updatedEvent);
        }

    }

    @XKasperUnregistered
    private static class EntityRepository extends Repository<TestAggregate> {

        @Override
        protected Optional<TestAggregate> doLoad(KasperID aggregateIdentifier, Long expectedVersion) {
            return null;
        }

        @Override
        protected void doSave(TestAggregate aggregate) { }

        @Override
        protected void doDelete(TestAggregate aggregate) { }

    }

    @XKasperUnregistered
    private static class EventRepository extends EventSourcedRepository<TestAggregate> {
        protected EventRepository(EventStore eventStore) {
            super(eventStore);
        }
    }

    // ------------------------------------------------------------------------

    @Test
    public void testAggregateStoredWithEntityRepository_shouldHaveEventsBeMarkedAsEventInfo() {
        // Given
        final Repository<TestAggregate> repo = new EntityRepository();
        repo.setEventBus(new SimpleEventBus());
        repo.init();
        CurrentUnitOfWork.set(DefaultUnitOfWork.startAndGet());

        // When
        final TestAggregate agr = new TestAggregate(DefaultContextBuilder.get(), DefaultKasperId.random());
        agr.makeSomeModification(DefaultContextBuilder.get());

        assertNotNull(agr.createdEvent);
        assertEquals(
                Event.PersistencyType.UNKNOWN,
                agr.createdEvent.getPersistencyType()
        );

        assertNotNull(agr.updatedEvent);
        assertEquals(
                Event.PersistencyType.UNKNOWN,
                agr.updatedEvent.getPersistencyType()
        );

        repo.add(agr);
        CurrentUnitOfWork.get().commit();

        // Then
        assertEquals(
                Event.PersistencyType.EVENT_INFO,
                agr.createdEvent.getPersistencyType()
        );
        assertEquals(
                Event.PersistencyType.EVENT_INFO,
                agr.updatedEvent.getPersistencyType()
        );

    }

    // ------------------------------------------------------------------------

    @Test
    public void testAggregateStoredWithEventRepository_shouldHaveEventsBeMarkedAsEventSource() {
        // Given
        final EventRepository repo = new EventRepository(mock(EventStore.class));
        repo.setEventBus(new SimpleEventBus());
        repo.init();
        CurrentUnitOfWork.set(DefaultUnitOfWork.startAndGet());

        // When
        final TestAggregate agr = new TestAggregate(DefaultContextBuilder.get(), DefaultKasperId.random());
        agr.makeSomeModification(DefaultContextBuilder.get());

        assertNotNull(agr.createdEvent);
        assertEquals(
                Event.PersistencyType.UNKNOWN,
                agr.createdEvent.getPersistencyType()
        );

        assertNotNull(agr.updatedEvent);
        assertEquals(
                Event.PersistencyType.UNKNOWN,
                agr.updatedEvent.getPersistencyType()
        );

        repo.add(agr);
        CurrentUnitOfWork.get().commit();

        // Then
        assertEquals(
                Event.PersistencyType.EVENT_SOURCE,
                agr.createdEvent.getPersistencyType()
        );
        assertEquals(
                Event.PersistencyType.EVENT_SOURCE,
                agr.updatedEvent.getPersistencyType()
        );

    }

}
