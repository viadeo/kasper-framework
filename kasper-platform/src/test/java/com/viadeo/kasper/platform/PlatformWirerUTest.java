// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.typesafe.config.Config;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.command.AutowiredCommandHandler;
import com.viadeo.kasper.core.component.command.MeasuredCommandHandler;
import com.viadeo.kasper.core.component.command.RepositoryManager;
import com.viadeo.kasper.core.component.command.gateway.KasperCommandGateway;
import com.viadeo.kasper.core.component.command.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.component.command.repository.Repository;
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.event.interceptor.EventInterceptorFactory;
import com.viadeo.kasper.core.component.event.listener.AutowiredEventListener;
import com.viadeo.kasper.core.component.event.listener.CommandEventListener;
import com.viadeo.kasper.core.component.event.listener.EventDescriptor;
import com.viadeo.kasper.core.component.event.listener.QueryEventListener;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.SagaExecutor;
import com.viadeo.kasper.core.component.event.saga.SagaManager;
import com.viadeo.kasper.core.component.event.saga.SagaWrapper;
import com.viadeo.kasper.core.component.query.MeasuredQueryHandler;
import com.viadeo.kasper.core.component.query.gateway.KasperQueryGateway;
import com.viadeo.kasper.core.component.query.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.platform.bundle.DomainBundle;
import com.viadeo.kasper.platform.bundle.descriptor.*;
import com.viadeo.kasper.platform.bundle.sample.MyCustomDomainBox;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;

import static org.junit.Assert.*;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PlatformWirerUTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private Config config;

    @Mock
    private MetricRegistry metricRegistry;

    @Mock
    private KasperEventBus eventBus;

    @Mock
    private KasperCommandGateway commandGateway;

    @Mock
    private KasperQueryGateway queryGateway;

    @Mock
    private SagaManager sagaManager;

    @Mock
    private RepositoryManager repositoryManager;

    private PlatformWirer platformWirer;

    @Before
    public void setUp() throws Exception {
        platformWirer = new PlatformWirer(config, metricRegistry, eventBus, commandGateway, queryGateway, sagaManager, repositoryManager);
        when(sagaManager.register(any(Saga.class))).thenReturn(mock(SagaExecutor.class));
    }

    @Test
    public void wire_a_bundle_should_return_his_description() {
        // Given
        final DomainBundle domainBundle = new DomainBundle.Builder(new MyCustomDomainBox.MyCustomDomain())
                .with(new MyCustomDomainBox.MyCustomCommandHandler())
                .with(new MyCustomDomainBox.MyCustomQueryHandler())
                .with(new MyCustomDomainBox.MyCustomEventListener())
                .with(new MyCustomDomainBox.MyCustomRepository())
                .build();

        // When
        DomainDescriptor domainDescriptor = platformWirer.wire(domainBundle);

        // Then
        assertNotNull(domainDescriptor);
        assertEquals(MyCustomDomainBox.MyCustomDomain.class, domainDescriptor.getDomainClass());
        assertTrue(domainDescriptor.getCommandHandlerDescriptors().contains(
                new CommandHandlerDescriptor(
                        MyCustomDomainBox.MyCustomCommandHandler.class,
                        MyCustomDomainBox.MyCustomCommand.class
                )
        ));
        assertTrue(domainDescriptor.getQueryHandlerDescriptors().contains(
                new QueryHandlerDescriptor(
                        MyCustomDomainBox.MyCustomQueryHandler.class,
                        MyCustomDomainBox.MyCustomQuery.class,
                        MyCustomDomainBox.MyCustomQueryResult.class
                )
        ));
        assertTrue(domainDescriptor.getEventListenerDescriptors().contains(
                new EventListenerDescriptor(
                        MyCustomDomainBox.MyCustomEventListener.class,
                        MyCustomDomainBox.MyCustomEvent.class
                )
        ));
        assertTrue(domainDescriptor.getRepositoryDescriptors().contains(
                new RepositoryDescriptor(
                        MyCustomDomainBox.MyCustomRepository.class,
                        new AggregateDescriptor(
                                MyCustomDomainBox.MyCustomEntity.class,
                                Lists.<Class<? extends Event>>newArrayList()
                        )
                )
        ));

    }

    @Test
    public void wire_a_bundle_containing_a_command_handler() {
        // Given
        final AutowiredCommandHandler commandHandler = spy(new MyCustomDomainBox.MyCustomCommandHandler());
        final DomainBundle domainBundle = new DomainBundle.Builder(mock(Domain.class))
                .with(commandHandler)
                .build();

        // When
        platformWirer.wire(domainBundle);

        // Then
        verify(commandGateway).register(isA(MeasuredCommandHandler.class));
        verify(commandHandler).setEventBus(refEq(eventBus));
        verify(commandHandler).setRepositoryManager(refEq(repositoryManager));
        verify(commandHandler).setCommandGateway(refEq(commandGateway));
        verify(commandHandler).getHandlerClass();
        verify(commandHandler).getInputClass();
        verifyNoMoreInteractions(commandHandler);
    }

    @Test
    public void wire_a_bundle_containing_an_query_handler() {
        // Given
        final MyCustomDomainBox.MyCustomQueryHandler queryHandler = spy(new MyCustomDomainBox.MyCustomQueryHandler());
        final DomainBundle domainBundle = new DomainBundle.Builder(mock(Domain.class))
                .with(queryHandler)
                .build();

        // When
        platformWirer.wire(domainBundle);

        // Then
        verify(queryGateway).register(isA(MeasuredQueryHandler.class));
        verify(queryHandler).setEventBus(refEq(eventBus));
        verify(queryHandler).setQueryGateway(refEq(queryGateway));
        verify(queryHandler).getHandlerClass();
        verify(queryHandler).getInputClass();
        verify(queryHandler).getResultClass();
        verifyNoMoreInteractions(queryHandler);
    }

    @Test
    public void wire_a_bundle_containing_an_event_listener() {
        // Given
        final AutowiredEventListener eventListener = spy(new MyCustomDomainBox.MyCustomEventListener());

        final DomainBundle domainBundle = new DomainBundle.Builder(mock(Domain.class))
                .with(eventListener)
                .build();

        // When
        platformWirer.wire(domainBundle);

        // Then
        verify(eventBus).subscribe(refEq(eventListener));
        verify(eventListener).setEventBus(refEq(eventBus));
        verify(eventListener).getHandlerClass();
        verify(eventListener).getEventDescriptors();
        verifyNoMoreInteractions(eventListener);
    }

    @Test
    public void wire_a_bundle_containing_a_command_event_listener() {
        // Given
        final CommandEventListener eventListener = mock(CommandEventListener.class);
        when(eventListener.getHandlerClass()).thenReturn(CommandEventListener.class);
        when(eventListener.getEventDescriptors()).thenReturn(Sets.<EventDescriptor>newHashSet(new EventDescriptor(Event.class)));

        final DomainBundle domainBundle = new DomainBundle.Builder(mock(Domain.class))
                .with(eventListener)
                .build();

        // When
        platformWirer.wire(domainBundle);

        // Then
        verify(eventBus).subscribe(refEq(eventListener));
        verify(eventListener).setEventBus(refEq(eventBus));
        verify(eventListener).setCommandGateway(refEq(commandGateway));
        verify(eventListener).getHandlerClass();
        verify(eventListener).getEventDescriptors();
        verifyNoMoreInteractions(eventListener);
    }

    @Test
    public void wire_a_bundle_containing_a_query_event_listener() {
        // Given
        final QueryEventListener eventListener = mock(QueryEventListener.class);
        when(eventListener.getHandlerClass()).thenReturn(QueryEventListener.class);
        when(eventListener.getEventDescriptors()).thenReturn(Sets.<EventDescriptor>newHashSet(new EventDescriptor(Event.class)));

        final DomainBundle domainBundle = new DomainBundle.Builder(mock(Domain.class))
                .with(eventListener)
                .build();

        // When
        platformWirer.wire(domainBundle);

        // Then
        verify(eventBus).subscribe(refEq(eventListener));
        verify(eventListener).setEventBus(refEq(eventBus));
        verify(eventListener).getHandlerClass();
        verify(eventListener).getEventDescriptors();
        verifyNoMoreInteractions(eventListener);
    }

    @Test
    public void wire_a_bundle_containing_a_repository() throws Exception {
        // Given
        final Repository repository = spy(new TestRepository());
        final DomainBundle domainBundle = new DomainBundle.Builder(mock(Domain.class))
                .with(repository)
                .build();

        // When
        platformWirer.wire(domainBundle);

        // Then
        verify(repositoryManager).register(refEq(repository));
        verify(repository).setEventBus(refEq(eventBus));
    }

    @Test
    public void wire_a_bundle_containing_a_saga() {
        // Given
        final Saga saga = mock(Saga.class);
        final DomainBundle domainBundle = new DomainBundle.Builder(mock(Domain.class))
                .with(saga)
                .build();

        // When
        platformWirer.wire(domainBundle);

        // Then
        verify(eventBus).subscribe(any(SagaWrapper.class));
        verify(sagaManager).register(refEq(saga));
    }

    @Test
    public void wire_a_query_interceptor() {
        // Given
        final QueryInterceptorFactory interceptorFactory = mock(QueryInterceptorFactory.class);

        // When
        platformWirer.wire(interceptorFactory);

        // Then
        verify(queryGateway).register(refEq(interceptorFactory));
        verifyNoMoreInteractions(queryGateway);
        verifyNoMoreInteractions(interceptorFactory);
    }

    @Test
    public void wire_a_command_interceptor() {
        // Given
        final CommandInterceptorFactory interceptorFactory = mock(CommandInterceptorFactory.class);

        // When
        platformWirer.wire(interceptorFactory);

        // Then
        verify(commandGateway).register(refEq(interceptorFactory));
        verifyNoMoreInteractions(commandGateway);
        verifyNoMoreInteractions(interceptorFactory);
    }

    @Test
    public void wire_an_event_interceptor() {
        // Given
        final EventInterceptorFactory interceptorFactory = mock(EventInterceptorFactory.class);

        // When
        platformWirer.wire(interceptorFactory);

        // Then
        verify(eventBus).register(refEq(interceptorFactory));
        verifyNoMoreInteractions(commandGateway);
        verifyNoMoreInteractions(interceptorFactory);
    }

    @Test
    public void wire_two_bundles_with_the_same_name_is_not_permitted() {
        // Given
        final DomainBundle bundleA = new DomainBundle.Builder(new MyCustomDomainBox.MyCustomDomain())
                .with(new MyCustomDomainBox.MyCustomCommandHandler())
                .with(new MyCustomDomainBox.MyCustomRepository())
                .build();

        final DomainBundle bundleB = new DomainBundle.Builder(new MyCustomDomainBox.MyCustomDomain())
                .with(new MyCustomDomainBox.MyCustomQueryHandler())
                .build();

        // Then
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Bundle name already wired : <name=MyCustomDomain>");

        // When
        platformWirer.wire(bundleA);
        platformWirer.wire(bundleB);
    }



    private static class TestRepository extends Repository<MyCustomDomainBox.MyCustomEntity> {

        public TestRepository() throws Exception {
            final Field declaredField = Repository.class.getDeclaredField("initialized");
            declaredField.setAccessible(true);
            declaredField.set(this, true);
        }

        @Override
        protected Optional<MyCustomDomainBox.MyCustomEntity> doLoad(final KasperID aggregateIdentifier,
                                               final Long expectedVersion) {
            return Optional.absent();
        }

        @Override
        protected void doSave(final MyCustomDomainBox.MyCustomEntity aggregate) { }

        @Override
        protected void doDelete(final MyCustomDomainBox.MyCustomEntity aggregate) { }

    }
}
