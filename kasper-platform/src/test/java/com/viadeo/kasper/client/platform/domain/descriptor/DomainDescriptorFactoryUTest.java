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
import org.junit.Assert;
import org.junit.Test;

public class DomainDescriptorFactoryUTest {

    @Test
    public void toCommandHandlerDescriptor_fromCommandHandler_shouldBeOk() {
        // Given

        // When
        CommandHandlerDescriptor descriptor = DomainDescriptorFactory.toCommandHandlerDescriptor(new TestCommandHandler());

        // Then
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(TestCommandHandler.class, descriptor.getReferenceClass());
        Assert.assertEquals(TestCommand.class, descriptor.getCommandClass());
    }

    @Test
    public void toQueryHandlerDescriptor_fromQueryHandler_shouldBeOk() {
        // Given

        // When
        QueryHandlerDescriptor descriptor = DomainDescriptorFactory.toQueryHandlerDescriptor(new TestQueryHandler());

        // Then
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(TestQueryHandler.class, descriptor.getReferenceClass());
        Assert.assertEquals(TestQuery.class, descriptor.getQueryClass());
        Assert.assertEquals(TestQueryResult.class, descriptor.getQueryResultClass());
    }

    @Test
    public void toEventListenerDescriptor_fromEventListener_shouldBeOk() {
        // Given

        // When
        EventListenerDescriptor descriptor = DomainDescriptorFactory.toEventListenerDescriptor(new TestEventListener());

        // Then
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(TestEventListener.class, descriptor.getReferenceClass());
        Assert.assertEquals(TestEvent.class, descriptor.getEventClass());
    }

    @Test
    public void toAggregateDescriptor_fromConcept_shouldBeOk() {
        // Given

        // When
        AggregateDescriptor descriptor = DomainDescriptorFactory.toAggregateDescriptor(TestConcept.class);

        // Then
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(TestConcept.class, descriptor.getReferenceClass());
        Assert.assertNull(descriptor.getSourceClass());
        Assert.assertNull(descriptor.getTargetClass());
        Assert.assertNotNull(descriptor.getSourceEventClasses());
        Assert.assertEquals(1, descriptor.getSourceEventClasses().size());
        Assert.assertTrue(descriptor.getSourceEventClasses().contains(TestEvent.class));
    }

    @Test
    public void toAggregateDescriptor_fromRelation_shouldBeOk() {
        // When
        AggregateDescriptor descriptor = DomainDescriptorFactory.toAggregateDescriptor(TestRelation.class);

        // Then
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(TestRelation.class, descriptor.getReferenceClass());
        Assert.assertEquals(TestConcept.class, descriptor.getSourceClass());
        Assert.assertEquals(TestConcept.class, descriptor.getTargetClass());
        Assert.assertNotNull(descriptor.getSourceEventClasses());
        Assert.assertEquals(1, descriptor.getSourceEventClasses().size());
        Assert.assertTrue(descriptor.getSourceEventClasses().contains(TestEvent.class));
    }

    @Test
    public void toRepositoryDescriptor_fromRepository_shouldBeOk() {
        // When
        RepositoryDescriptor descriptor = DomainDescriptorFactory.toRepositoryDescriptor(new TestRepository());

        // Then
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(TestRepository.class, descriptor.getReferenceClass());
        AggregateDescriptor aggregateDescriptor = descriptor.getAggregateDescriptor();
        Assert.assertNotNull(aggregateDescriptor);
        Assert.assertEquals(TestConcept.class, aggregateDescriptor.getReferenceClass());
    }

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
        public void doStuff(TestEvent event) {
        }
    }

    public static class TestRelation extends Relation<TestConcept, TestConcept> {
        @EventHandler
        public void doStuff(TestEvent event) {
        }
    }

    public static class TestRepository extends Repository<TestConcept> {
        @Override
        protected Optional<TestConcept> doLoad(KasperID aggregateIdentifier, Long expectedVersion) {
            return Optional.absent();
        }

        @Override
        protected void doSave(TestConcept aggregate) {
        }

        @Override
        protected void doDelete(TestConcept aggregate) {
        }
    }

    private static class TestDomain implements Domain {}

}
