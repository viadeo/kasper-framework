// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.eventbus.KasperEventBus;
import com.viadeo.kasper.client.platform.configuration.KasperPlatformConfiguration;
import com.viadeo.kasper.client.platform.bundle.DefaultDomainBundle;
import com.viadeo.kasper.client.platform.bundle.DomainBundle;
import com.viadeo.kasper.client.platform.bundle.descriptor.*;
import com.viadeo.kasper.client.platform.plugin.Plugin;
import com.viadeo.kasper.core.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.interceptor.EventInterceptorFactory;
import com.viadeo.kasper.core.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.core.resolvers.DomainHelper;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.command.RepositoryManager;
import com.viadeo.kasper.core.component.command.DefaultRepositoryManager;
import com.viadeo.kasper.core.component.command.gateway.KasperCommandGateway;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.gateway.KasperQueryGateway;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.core.component.command.repository.Repository;
import com.viadeo.kasper.core.component.command.aggregate.Concept;
import com.viadeo.kasper.core.component.event.CommandEventListener;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.core.component.event.EventListener;
import com.viadeo.kasper.core.component.event.QueryEventListener;
import com.viadeo.kasper.core.component.saga.SagaManager;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;

import static com.viadeo.kasper.client.platform.Platform.ExtraComponentKey;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.*;

public class PlatformBuilderUTest {

    private static class TestDomain implements Domain { }

    private static class TestConcept extends Concept {
        private static final long serialVersionUID = -7248313390394661735L;
    }

    private static class TestRepository extends Repository<TestConcept> {

        public TestRepository() throws Exception {
            final Field declaredField = Repository.class.getDeclaredField("initialized");
            declaredField.setAccessible(true);
            declaredField.set(this, true);
        }

        @Override
        protected Optional<TestConcept> doLoad(final KasperID aggregateIdentifier,
                                               final Long expectedVersion) {
            return Optional.absent();
        }

        @Override
        protected void doSave(final TestConcept aggregate) { }

        @Override
        protected void doDelete(final TestConcept aggregate) { }

    }

    // ------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void addDomainBundle_withNullAsDomainBundle_shouldThrownException() {
        // Given
        final DomainBundle domainBundle = null;
        final Platform.Builder builder = new Platform.Builder();

        // When
        builder.addDomainBundle(domainBundle);

        // Then throws an exception
    }

    @Test
    public void addDomainBundle_withDomainBundle_shouldBeOk() {
        // Given
        final DomainBundle domainBundle = new DefaultDomainBundle(new TestDomain());
        final Platform.Builder builder = new Platform.Builder();

        // When
        builder.addDomainBundle(domainBundle);

        // Then no exception
    }

    @Test(expected = NullPointerException.class)
    public void addPlugin_withNullAsPlugin_shouldThrownException() {
        // Given
        final Plugin plugin = null;
        final Platform.Builder builder = new Platform.Builder();

        // When
        builder.addPlugin(plugin);

        // Then throws an exception
    }

    @Test
    public void addPlugin_withPlugin_shouldBeOk() {
        // Given
        final Plugin plugin = mock(Plugin.class);
        final Platform.Builder builder = new Platform.Builder();

        // When
        builder.addPlugin(plugin);

        // Then no exception
    }

    @Test(expected = NullPointerException.class)
    public void addQueryInterceptorFactory_withNullAsInterceptorFactory_shouldThrownException() {
        // Given
        final QueryInterceptorFactory interceptorFactory = null;
        final Platform.Builder builder = new Platform.Builder();

        // When
        builder.addQueryInterceptorFactory(interceptorFactory);

        // Then throws exception
    }

    @Test(expected = NullPointerException.class)
    public void addCommandInterceptorFactory_withNullAsInterceptorFactory_shouldThrownException() {
        // Given
        final CommandInterceptorFactory interceptorFactory = null;
        final Platform.Builder builder = new Platform.Builder();

        // When
        builder.addCommandInterceptorFactory(interceptorFactory);

        // Then throws exception
    }

    @Test(expected = NullPointerException.class)
    public void addEventInterceptorFactory_withNullAsInterceptorFactory_shouldThrownException() {
        // Given
        final EventInterceptorFactory interceptorFactory = null;
        final Platform.Builder builder = new Platform.Builder();

        // When
        builder.addEventInterceptorFactory(interceptorFactory);

        // Then throws exception
    }

    @Test
    public void registerInterceptors_withQueryInterceptorFactory_shouldBeRegisteredOnQueryGateway() {
        // Given
        final QueryInterceptorFactory interceptorFactory = mock(QueryInterceptorFactory.class);
        final KasperQueryGateway queryGateway = mock(KasperQueryGateway.class);
        final Platform.Builder builder = new Platform.Builder()
                .withQueryGateway(queryGateway)
                .addQueryInterceptorFactory(interceptorFactory);

        // When
        builder.registerInterceptors();

        // Then
        verify(queryGateway).register(refEq(interceptorFactory));
        verifyNoMoreInteractions(queryGateway);
        verifyNoMoreInteractions(interceptorFactory);
    }

    @Test
    public void registerInterceptors_withCommandInterceptorFactory_shouldBeRegisteredOnCommandGateway() {
        // Given
        final CommandInterceptorFactory interceptorFactory = mock(CommandInterceptorFactory.class);
        final KasperCommandGateway commandGateway = mock(KasperCommandGateway.class);
        final Platform.Builder builder = new Platform.Builder()
                .withCommandGateway(commandGateway)
                .addCommandInterceptorFactory(interceptorFactory);

        // When
        builder.registerInterceptors();

        // Then
        verify(commandGateway).register(refEq(interceptorFactory));
        verifyNoMoreInteractions(commandGateway);
        verifyNoMoreInteractions(interceptorFactory);
    }

    @Test(expected = IllegalStateException.class)
    public void build_withoutCommandGateway_shouldThrownException() {
        // Given
        final Platform.Builder builder = new Platform.Builder()
                .withQueryGateway(mock(KasperQueryGateway.class))
                .withEventBus(mock(KasperEventBus.class))
                .withConfiguration(mock(Config.class));

        // When
        builder.build();

        // Then throws an exception
    }

    @Test(expected = IllegalStateException.class)
    public void build_withoutQueryGateway_shouldThrownException() {
        // Given
        final Platform.Builder builder = new Platform.Builder()
                .withCommandGateway(mock(KasperCommandGateway.class))
                .withEventBus(mock(KasperEventBus.class))
                .withConfiguration(mock(Config.class));

        // When
        builder.build();

        // Then throws an exception
    }

    @Test(expected = IllegalStateException.class)
    public void build_withoutEventBus_shouldThrownException() {
        // Given
        final Platform.Builder builder = new Platform.Builder()
                .withQueryGateway(mock(KasperQueryGateway.class))
                .withCommandGateway(mock(KasperCommandGateway.class))
                .withConfiguration(mock(Config.class));

        // When
        builder.build();

        // Then throws an exception

    }

    @Test(expected = IllegalStateException.class)
    public void build_withoutConfiguration_shouldThrownException() {
        // Given
        final Platform.Builder builder = new Platform.Builder()
                .withQueryGateway(mock(KasperQueryGateway.class))
                .withCommandGateway(mock(KasperCommandGateway.class))
                .withEventBus(mock(KasperEventBus.class));

        // When
        builder.build();

        // Then throws an exception
    }

    @Test(expected = IllegalStateException.class)
    public void build_withoutMetricRegistry_shouldThrownException() {
        // Given
        final Platform.Builder builder = new Platform.Builder()
                .withQueryGateway(mock(KasperQueryGateway.class))
                .withCommandGateway(mock(KasperCommandGateway.class))
                .withEventBus(mock(KasperEventBus.class))
                .withConfiguration(mock(Config.class));

        // When
        builder.build();

        // Then throws an exception
    }

    @Test
    public void build_withQueryGateway_withCommandGateway_withEventBus_withConfiguration_withMetricRegistry_shouldBeOk() {
        // Given
        final KasperEventBus eventBus = mock(KasperEventBus.class);
        final KasperCommandGateway commandGateway = mock(KasperCommandGateway.class);
        final KasperQueryGateway queryGateway = mock(KasperQueryGateway.class);

        final Platform.Builder builder = new Platform.Builder()
                .withQueryGateway(queryGateway)
                .withCommandGateway(commandGateway)
                .withEventBus(eventBus)
                .withSagaManager(mock(SagaManager.class))
                .withConfiguration(mock(Config.class))
                .withMetricRegistry(mock(MetricRegistry.class));

        // When
        final Platform platform = builder.build();

        // Then
        assertNotNull(platform);
        assertEquals(eventBus, platform.getEventBus());
        assertEquals(commandGateway, platform.getCommandGateway());
        assertEquals(queryGateway, platform.getQueryGateway());
    }

    @Test
    public void build_fromPlatformConfiguration_shouldBeOk() {
        // Given
        final KasperPlatformConfiguration platformConfiguration = new KasperPlatformConfiguration();
        final Platform.Builder builder = new Platform.Builder(platformConfiguration);

        // When
        final Platform platform = builder.build();

        // Then
        assertNotNull(platform);
        assertEquals(platformConfiguration.commandGateway(), platform.getCommandGateway());
        assertEquals(platformConfiguration.queryGateway(), platform.getQueryGateway());
        assertEquals(platformConfiguration.eventBus(), platform.getEventBus());
    }

    @Test
    public void build_withDomainBundle_shouldConfiguredTheBundle() {
        // Given
        final DomainBundle domainBundle = spy(new DomainBundle.Builder(new TestDomain()).build());

        final KasperEventBus eventBus = mock(KasperEventBus.class);
        final KasperCommandGateway commandGateway = mock(KasperCommandGateway.class);
        final KasperQueryGateway queryGateway = mock(KasperQueryGateway.class);
        final Config configuration = mock(Config.class);
        final MetricRegistry metricRegistry = mock(MetricRegistry.class);

        final Platform.Builder builder = new Platform.Builder()
                .withQueryGateway(queryGateway)
                .withCommandGateway(commandGateway)
                .withEventBus(eventBus)
                .withSagaManager(mock(SagaManager.class))
                .withConfiguration(configuration)
                .withMetricRegistry(metricRegistry)
                .addDomainBundle(domainBundle);

        // When
        final Platform platform = builder.build();

        // Then
        assertNotNull(platform);
        verify(domainBundle).configure(refEq(new Platform.BuilderContext(configuration, eventBus, commandGateway, queryGateway, metricRegistry, Maps.<ExtraComponentKey, Object>newHashMap())));
    }

    @Test
    public void build_withPlugin_shouldInitializedThePlugin() {
        // Given
        final Plugin plugin = mock(Plugin.class);
        final KasperEventBus eventBus = mock(KasperEventBus.class);
        final KasperCommandGateway commandGateway = mock(KasperCommandGateway.class);
        final KasperQueryGateway queryGateway = mock(KasperQueryGateway.class);
        final Config configuration = mock(Config.class);
        final MetricRegistry metricRegistry = mock(MetricRegistry.class);

        final Platform.Builder builder = new Platform.Builder()
                .withQueryGateway(queryGateway)
                .withCommandGateway(commandGateway)
                .withEventBus(eventBus)
                .withSagaManager(mock(SagaManager.class))
                .withConfiguration(configuration)
                .withMetricRegistry(metricRegistry)
                .addPlugin(plugin);

        // When
        final Platform platform = builder.build();

        // Then
        assertNotNull(platform);
        verify(plugin).initialize(refEq(platform), refEq(metricRegistry), (DomainDescriptor[]) anyVararg());
    }

    @Test
    public void build_withQueryInterceptorFactory_shouldRegisterOnQueryGateway() {
        // Given
        final QueryInterceptorFactory queryInterceptorFactory = mock(QueryInterceptorFactory.class);
        final KasperQueryGateway queryGateway = mock(KasperQueryGateway.class);

        final Platform.Builder builder = new Platform.Builder(new KasperPlatformConfiguration())
                .withQueryGateway(queryGateway)
                .addQueryInterceptorFactory(queryInterceptorFactory);

        // When
        final Platform platform = builder.build();

        // Then
        assertNotNull(platform);
        verify(queryGateway).register(refEq(queryInterceptorFactory));
    }

    @Test
    public void build_withCommandInterceptorFactory_shouldRegisterOnCommandGateway() {
        // Given
        final CommandInterceptorFactory commandInterceptorFactory = mock(CommandInterceptorFactory.class);
        final KasperCommandGateway commandGateway = mock(KasperCommandGateway.class);

        final Platform.Builder builder = new Platform.Builder(new KasperPlatformConfiguration())
                .withCommandGateway(commandGateway)
                .addCommandInterceptorFactory(commandInterceptorFactory);

        // When
        final Platform platform = builder.build();

        // Then
        assertNotNull(platform);
        verify(commandGateway).register(refEq(commandInterceptorFactory));
    }

    @Test
    public void build_withEventInterceptorFactory_shouldRegisterOnEventBus() {
        // Given
        final EventInterceptorFactory eventInterceptorFactory = mock(EventInterceptorFactory.class);
        final KasperEventBus eventBus = mock(KasperEventBus.class);

        final Platform.Builder builder = new Platform.Builder(new KasperPlatformConfiguration())
                .withEventBus(eventBus)
                .addEventInterceptorFactory(eventInterceptorFactory);

        // When
        final Platform platform = builder.build();

        // Then
        assertNotNull(platform);
        verify(eventBus).register(refEq(eventInterceptorFactory));
    }

    @Test
    public void build_withDomainBundle_containingCommandHandler_shouldWiredTheComponent() {
        // Given
        final CommandHandler commandHandler = mock(CommandHandler.class);

        final DomainBundle domainBundle = new DomainBundle.Builder(new TestDomain())
                .with(commandHandler)
                .build();

        final KasperEventBus eventBus = mock(KasperEventBus.class);
        final KasperCommandGateway commandGateway = mock(KasperCommandGateway.class);
        final DomainDescriptorFactory domainDescriptorFactory = createMockedDomainDescriptorFactory();

        final Platform.Builder builder = new Platform.Builder(domainDescriptorFactory)
                .withQueryGateway(mock(KasperQueryGateway.class))
                .withCommandGateway(commandGateway)
                .withEventBus(eventBus)
                .withSagaManager(mock(SagaManager.class))
                .withConfiguration(mock(Config.class))
                .withMetricRegistry(mock(MetricRegistry.class))
                .addDomainBundle(domainBundle);

        // When
        final Platform platform = builder.build();

        // Then
        assertNotNull(platform);
        verify(commandGateway).register(refEq(commandHandler));
        verify(commandHandler).setEventBus(refEq(eventBus));
    }

    @Test
    public void build_withDomainBundle_containingQueryHandler_shouldWiredTheComponent() {
        // Given
        final QueryHandler queryHandler = mock(QueryHandler.class);

        final DomainBundle domainBundle = new DomainBundle.Builder(new TestDomain())
                .with(queryHandler)
                .build();

        final KasperEventBus eventBus = mock(KasperEventBus.class);
        final KasperQueryGateway queryGateway = mock(KasperQueryGateway.class);
        final DomainDescriptorFactory domainDescriptorFactory = createMockedDomainDescriptorFactory();

        final Platform.Builder builder = new Platform.Builder(domainDescriptorFactory)
                .withQueryGateway(queryGateway)
                .withCommandGateway(mock(KasperCommandGateway.class))
                .withEventBus(eventBus)
                .withSagaManager(mock(SagaManager.class))
                .withConfiguration(mock(Config.class))
                .withMetricRegistry(mock(MetricRegistry.class))
                .addDomainBundle(domainBundle);

        // When
        final Platform platform = builder.build();

        // Then
        assertNotNull(platform);
        verify(queryGateway).register(refEq(queryHandler));
    }

    @Test
    public void build_withDomainBundle_containingEventListener_shouldWiredTheComponent() {
        // Given
        final EventListener eventListener = mock(EventListener.class);

        final DomainBundle domainBundle = new DomainBundle.Builder(new TestDomain())
                .with(eventListener)
                .build();

        final KasperEventBus eventBus = mock(KasperEventBus.class);
        final KasperCommandGateway commandGateway = mock(KasperCommandGateway.class);
        final DomainDescriptorFactory domainDescriptorFactory = createMockedDomainDescriptorFactory();

        final Platform.Builder builder = new Platform.Builder(domainDescriptorFactory)
                .withQueryGateway(mock(KasperQueryGateway.class))
                .withCommandGateway(commandGateway)
                .withEventBus(eventBus)
                .withSagaManager(mock(SagaManager.class))
                .withConfiguration(mock(Config.class))
                .withMetricRegistry(mock(MetricRegistry.class))
                .addDomainBundle(domainBundle);

        // When
        final Platform platform = builder.build();

        // Then
        assertNotNull(platform);
        verify(eventBus).subscribe(refEq(eventListener));
        verify(eventListener).setEventBus(refEq(eventBus));
        verifyNoMoreInteractions(eventListener);
    }

    @Test
    public void build_withDomainBundle_containingCommandEventListener_shouldWiredTheComponent() {
        // Given
        final CommandEventListener eventListener = mock(CommandEventListener.class);

        final DomainBundle domainBundle = new DomainBundle.Builder(new TestDomain())
                .with(eventListener)
                .build();

        final KasperEventBus eventBus = mock(KasperEventBus.class);
        final KasperCommandGateway commandGateway = mock(KasperCommandGateway.class);
        final DomainDescriptorFactory domainDescriptorFactory = createMockedDomainDescriptorFactory();

        final Platform.Builder builder = new Platform.Builder(domainDescriptorFactory)
                .withQueryGateway(mock(KasperQueryGateway.class))
                .withCommandGateway(commandGateway)
                .withEventBus(eventBus)
                .withSagaManager(mock(SagaManager.class))
                .withConfiguration(mock(Config.class))
                .withMetricRegistry(mock(MetricRegistry.class))
                .addDomainBundle(domainBundle);

        // When
        final Platform platform = builder.build();

        // Then
        assertNotNull(platform);
        verify(eventBus).subscribe(refEq(eventListener));
        verify(eventListener).setEventBus(refEq(eventBus));
        verify(eventListener).setCommandGateway(refEq(commandGateway));
        verifyNoMoreInteractions(eventListener);
    }

    @Test
    public void build_withDomainBundle_containingQueryEventListener_shouldWiredTheComponent() {
        // Given
        final QueryEventListener eventListener = mock(QueryEventListener.class);

        final DomainBundle domainBundle = new DomainBundle.Builder(new TestDomain())
                .with(eventListener)
                .build();

        final KasperEventBus eventBus = mock(KasperEventBus.class);
        final KasperQueryGateway queryGateway = mock(KasperQueryGateway.class);
        final DomainDescriptorFactory domainDescriptorFactory = createMockedDomainDescriptorFactory();

        final Platform.Builder builder = new Platform.Builder(domainDescriptorFactory)
                .withQueryGateway(mock(KasperQueryGateway.class))
                .withCommandGateway(mock(KasperCommandGateway.class))
                .withEventBus(eventBus)
                .withSagaManager(mock(SagaManager.class))
                .withConfiguration(mock(Config.class))
                .withMetricRegistry(mock(MetricRegistry.class))
                .addDomainBundle(domainBundle);

        // When
        final Platform platform = builder.build();

        // Then
        assertNotNull(platform);
        verify(eventBus).subscribe(refEq(eventListener));
        verify(eventListener).setEventBus(refEq(eventBus));
        verifyNoMoreInteractions(eventListener);
    }

    @Test
    public void build_withDomainBundle_containingRepository_shouldWiredTheComponent() throws Exception {
        // Given
        final Repository repository = spy(new TestRepository());

        final DomainBundle domainBundle = new DomainBundle.Builder(new TestDomain())
                .with(repository)
                .build();

        final KasperEventBus eventBus = mock(KasperEventBus.class);
        final KasperCommandGateway commandGateway = mock(KasperCommandGateway.class);
        final DomainDescriptorFactory domainDescriptorFactory = createMockedDomainDescriptorFactory();
        final RepositoryManager repositoryManager = mock(DefaultRepositoryManager.class);

        final Platform.Builder builder = new Platform.Builder(domainDescriptorFactory)
                .withQueryGateway(mock(KasperQueryGateway.class))
                .withCommandGateway(commandGateway)
                .withEventBus(eventBus)
                .withSagaManager(mock(SagaManager.class))
                .withConfiguration(mock(Config.class))
                .withRepositoryManager(repositoryManager)
                .withMetricRegistry(mock(MetricRegistry.class))
                .addDomainBundle(domainBundle);

        // When
        final Platform platform = builder.build();

        // Then
        assertNotNull(platform);
        verify(repository).setEventBus(refEq(eventBus));
        verify(repositoryManager).register(refEq(repository));
    }

    @Test
    public void build_withExtraComponent_shouldConfiguredTheBundle() throws Exception {
        // Given
        final DomainBundle domainBundle = spy(new DomainBundle.Builder(new TestDomain()).build());

        final KasperEventBus eventBus = mock(KasperEventBus.class);
        final KasperCommandGateway commandGateway = mock(KasperCommandGateway.class);
        final KasperQueryGateway queryGateway = mock(KasperQueryGateway.class);
        final Config configuration = mock(Config.class);
        final MetricRegistry metricRegistry = mock(MetricRegistry.class);

        final Platform.Builder builder = new Platform.Builder()
                .withQueryGateway(queryGateway)
                .withCommandGateway(commandGateway)
                .withEventBus(eventBus)
                .withSagaManager(mock(SagaManager.class))
                .withConfiguration(configuration)
                .withMetricRegistry(metricRegistry)
                .addDomainBundle(domainBundle);

        final String name = "test";
        final String component = String.valueOf("hiha!!!");

        // When
        final Platform platform = builder.addExtraComponent(name, String.class, component).build();

        // Then
        final HashMap<ExtraComponentKey, Object> expectedExtraComponents = Maps.newHashMap();
        expectedExtraComponents.put(new ExtraComponentKey(name, component.getClass()), component);

        assertNotNull(platform);
        verify(domainBundle).configure(refEq(new Platform.BuilderContext(configuration, eventBus, commandGateway, queryGateway, metricRegistry, expectedExtraComponents)));
    }

    @Test
    public void build_withDomainBundle_containingQueryInterceptorFactory_shouldBeRegistered() {
        // Given
        final QueryInterceptorFactory queryInterceptorFactory = mock(QueryInterceptorFactory.class);
        final DomainBundle domainBundle = new DomainBundle.Builder(new TestDomain())
                .with(queryInterceptorFactory)
                .build();

        final KasperQueryGateway queryGateway = mock(KasperQueryGateway.class);

        final Platform.Builder builder = new Platform.Builder(new KasperPlatformConfiguration())
                .withQueryGateway(queryGateway)
                .addDomainBundle(domainBundle);

        // When
        final Platform platform = builder.build();

        // Then
        assertNotNull(platform);
        verify(queryGateway).register(refEq(queryInterceptorFactory));
    }

    @Test
    public void build_withDomainBundle_containingCommandInterceptorFactory_shouldBeRegistered() {
        // Given
        final CommandInterceptorFactory commandInterceptorFactory = mock(CommandInterceptorFactory.class);
        final DomainBundle domainBundle = new DomainBundle.Builder(new TestDomain())
                .with(commandInterceptorFactory)
                .build();

        final KasperCommandGateway commandGateway = mock(KasperCommandGateway.class);

        final Platform.Builder builder = new Platform.Builder(new KasperPlatformConfiguration())
                .withCommandGateway(commandGateway)
                .addDomainBundle(domainBundle);

        // When
        final Platform platform = builder.build();

        // Then
        assertNotNull(platform);
        verify(commandGateway).register(refEq(commandInterceptorFactory));
    }

    @Test
    public void build_withDomainBundle_containingEventInterceptorFactory_shouldBeRegistered() {
        // Given
        final EventInterceptorFactory eventInterceptorFactory = mock(EventInterceptorFactory.class);
        final DomainBundle domainBundle = new DomainBundle.Builder(new TestDomain())
                .with(eventInterceptorFactory)
                .build();

        final KasperEventBus eventBus = mock(KasperEventBus.class);

        final Platform.Builder builder = new Platform.Builder(new KasperPlatformConfiguration())
                .withEventBus(eventBus)
                .addDomainBundle(domainBundle);

        // When
        final Platform platform = builder.build();

        // Then
        assertNotNull(platform);
        verify(eventBus).register(refEq(eventInterceptorFactory));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void configureDomainBundle_withBundle_shouldAddDescriptorsToDomainHelper() {
        // Given
        final DomainHelper domainHelper = mock(DomainHelper.class);

        final Platform.Builder builder = new Platform.Builder(new KasperPlatformConfiguration());
        builder.setDomainHelper(domainHelper);

        final DomainBundle domainBundle = new DomainBundle.Builder(new TestDomain()).build();
        final Platform.BuilderContext builderContext = mock(Platform.BuilderContext.class);

        // When
        final DomainDescriptor descriptor = builder.configureDomainBundle(builderContext, domainBundle);

        // Then
        assertNotNull(descriptor);
        verify(domainHelper).add(anyMap());
    }

    private DomainDescriptorFactory createMockedDomainDescriptorFactory() {
        final DomainDescriptorFactory domainDescriptorFactory = mock(DomainDescriptorFactory.class);
        when(domainDescriptorFactory.createFrom(any(DomainBundle.class))).thenReturn(
                new DomainDescriptor(
                    "FakeDomain",
                    Domain.class,
                    Lists.<QueryHandlerDescriptor>newArrayList(),
                    Lists.<CommandHandlerDescriptor>newArrayList(),
                    Lists.<RepositoryDescriptor>newArrayList(),
                    Lists.<EventListenerDescriptor>newArrayList(),
                    Lists.<SagaDescriptor>newArrayList(),
                    Lists.<Class<? extends Event>>newArrayList()
                )
        );
        return domainDescriptorFactory;
    }

}
