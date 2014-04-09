// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.domain.descriptor;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.er.Concept;
import com.viadeo.kasper.er.Relation;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.event.annotation.XKasperEventListener;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.junit.Test;

import static org.junit.Assert.*;

public class DomainDescriptorFactoryUTest {

    public static class TestCommand implements Command { }

    @XKasperCommandHandler(domain = TestDomain.class)
    public static class TestCommandHandler extends CommandHandler<TestCommand> { }

    public static class TestQuery implements Query { }

    public static class TestQueryResult implements QueryResult { }

    @XKasperQueryHandler(domain = TestDomain.class)
    public static class TestQueryHandler extends QueryHandler<TestQuery, TestQueryResult> { }

    public static class TestEvent extends Event { }

    @XKasperEventListener(domain = TestDomain.class)
    public static class TestEventListener extends EventListener<TestEvent> { }

    public static class TestConcept extends Concept {
        @EventHandler
        public void doStuff(final TestEvent event) {
        }
    }

    public static class TestRelation extends Relation<TestConcept, TestConcept> {
        @EventHandler
        public void doStuff(final TestEvent event) {
        }
    }

    public static class TestRepository extends Repository<TestConcept> {
        @Override
        protected Optional<TestConcept> doLoad(final KasperID aggregateIdentifier, final Long expectedVersion) {
            return Optional.absent();
        }

        @Override
        protected void doSave(final TestConcept aggregate) {
        }

        @Override
        protected void doDelete(final TestConcept aggregate) {
        }
    }

    private static class TestDomain implements Domain {}

    // ------------------------------------------------------------------------

    @Test
    public void toCommandHandlerDescriptor_fromCommandHandler_shouldBeOk() {
        // Given

        // When
        final CommandHandlerDescriptor descriptor =
                DomainDescriptorFactory.toCommandHandlerDescriptor(
                    new TestCommandHandler()
                );

        // Then
        assertNotNull(descriptor);
        assertEquals(TestCommandHandler.class, descriptor.getReferenceClass());
        assertEquals(TestCommand.class, descriptor.getCommandClass());
    }

    @Test
    public void toQueryHandlerDescriptor_fromQueryHandler_shouldBeOk() {
        // Given

        // When
        final QueryHandlerDescriptor descriptor =
                DomainDescriptorFactory.toQueryHandlerDescriptor(
                    new TestQueryHandler()
                );

        // Then
        assertNotNull(descriptor);
        assertEquals(TestQueryHandler.class, descriptor.getReferenceClass());
        assertEquals(TestQuery.class, descriptor.getQueryClass());
        assertEquals(TestQueryResult.class, descriptor.getQueryResultClass());
    }

    @Test
    public void toEventListenerDescriptor_fromEventListener_shouldBeOk() {
        // Given

        // When
        final EventListenerDescriptor descriptor =
                DomainDescriptorFactory.toEventListenerDescriptor(
                    new TestEventListener()
                );

        // Then
        assertNotNull(descriptor);
        assertEquals(TestEventListener.class, descriptor.getReferenceClass());
        assertEquals(TestEvent.class, descriptor.getEventClass());
    }

    @Test
    public void toAggregateDescriptor_fromConcept_shouldBeOk() {
        // Given

        // When
        final AggregateDescriptor descriptor =
                DomainDescriptorFactory.toAggregateDescriptor(
                    TestConcept.class
                );

        // Then
        assertNotNull(descriptor);
        assertEquals(TestConcept.class, descriptor.getReferenceClass());
        assertNull(descriptor.getSourceClass());
        assertNull(descriptor.getTargetClass());
        assertNotNull(descriptor.getSourceEventClasses());
        assertEquals(1, descriptor.getSourceEventClasses().size());
        assertTrue(descriptor.getSourceEventClasses().contains(TestEvent.class));
    }

    @Test
    public void toAggregateDescriptor_fromRelation_shouldBeOk() {
        // When
        final AggregateDescriptor descriptor =
                DomainDescriptorFactory.toAggregateDescriptor(
                    TestRelation.class
                );

        // Then
        assertNotNull(descriptor);
        assertEquals(TestRelation.class, descriptor.getReferenceClass());
        assertEquals(TestConcept.class, descriptor.getSourceClass());
        assertEquals(TestConcept.class, descriptor.getTargetClass());
        assertNotNull(descriptor.getSourceEventClasses());
        assertEquals(1, descriptor.getSourceEventClasses().size());
        assertTrue(descriptor.getSourceEventClasses().contains(TestEvent.class));
    }

    @Test
    public void toRepositoryDescriptor_fromRepository_shouldBeOk() {
        // When
        final RepositoryDescriptor descriptor =
                DomainDescriptorFactory.toRepositoryDescriptor(
                    new TestRepository()
                );

        // Then
        assertNotNull(descriptor);
        assertEquals(TestRepository.class, descriptor.getReferenceClass());
        final AggregateDescriptor aggregateDescriptor = descriptor.getAggregateDescriptor();
        assertNotNull(aggregateDescriptor);
        assertEquals(TestConcept.class, aggregateDescriptor.getReferenceClass());
    }

}
