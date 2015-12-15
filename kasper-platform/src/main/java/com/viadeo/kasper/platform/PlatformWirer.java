// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform;

import com.codahale.metrics.MetricRegistry;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.typesafe.config.Config;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.command.MeasuredCommandHandler;
import com.viadeo.kasper.core.component.command.RepositoryManager;
import com.viadeo.kasper.core.component.command.WirableCommandHandler;
import com.viadeo.kasper.core.component.command.gateway.KasperCommandGateway;
import com.viadeo.kasper.core.component.command.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.component.command.repository.Repository;
import com.viadeo.kasper.core.component.command.repository.WirableRepository;
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.event.interceptor.EventInterceptorFactory;
import com.viadeo.kasper.core.component.event.listener.CommandEventListener;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.core.component.event.listener.WirableEventListener;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.SagaExecutor;
import com.viadeo.kasper.core.component.event.saga.SagaManager;
import com.viadeo.kasper.core.component.event.saga.SagaWrapper;
import com.viadeo.kasper.core.component.query.MeasuredQueryHandler;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.WirableQueryHandler;
import com.viadeo.kasper.core.component.query.gateway.KasperQueryGateway;
import com.viadeo.kasper.core.component.query.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.platform.builder.PlatformContext;
import com.viadeo.kasper.platform.bundle.DomainBundle;
import com.viadeo.kasper.platform.bundle.descriptor.DomainDescriptor;
import com.viadeo.kasper.platform.bundle.descriptor.DomainDescriptorFactory;
import com.viadeo.kasper.platform.plugin.Plugin;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.axonframework.domain.DomainEventMessage;
import org.axonframework.domain.DomainEventStream;
import org.axonframework.domain.SimpleDomainEventStream;
import org.axonframework.eventstore.EventStore;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class PlatformWirer {

    private final EventStore eventStore;
    private final KasperEventBus eventBus;
    private final KasperCommandGateway commandGateway;
    private final KasperQueryGateway queryGateway;
    private final SagaManager sagaManager;
    private final RepositoryManager repositoryManager;
    private final DomainDescriptorFactory domainDescriptorFactory;
    private final List<ExtraComponent> extraComponents;

    private final Config config;
    private final MetricRegistry metricRegistry;
    private final Set<String> registeredBundleNames;
    private final Map<String,Plugin> registeredPlugins;
    private final Meta meta;

    // ------------------------------------------------------------------------

    static class AggregateTypedEventMessage {
        String type;
        DomainEventMessage<?> eventMessage;
    }

    public PlatformWirer(
            final Config config,
            final MetricRegistry metricRegistry,
            final KasperEventBus eventBus,
            final KasperCommandGateway commandGateway,
            final KasperQueryGateway queryGateway,
            final SagaManager sagaManager,
            final RepositoryManager repositoryManager,
            final Meta meta
    ) {
        this(
                config,
                metricRegistry,
                eventBus,
                commandGateway,
                queryGateway,
                sagaManager,
                repositoryManager,
                meta,

                // FIXME quick and dirty fix to avoid memory leaks
                new EventStore() {

                    private CircularFifoBuffer queue = new CircularFifoBuffer(10);


                    @Override
                    public synchronized void appendEvents(String type, DomainEventStream events) {
                        while (events.hasNext()) {
                            AggregateTypedEventMessage obj = new AggregateTypedEventMessage();
                            obj.type = type;
                            obj.eventMessage = events.next();
                            queue.add(obj);
                        }
                    }

                    @Override
                    public synchronized DomainEventStream readEvents(String type, Object identifier) {
                        ArrayList<DomainEventMessage<?>> selection = new ArrayList<>();
                        for (Object o : queue) {
                            AggregateTypedEventMessage typedMessage = (AggregateTypedEventMessage)o;
                            if (typedMessage.type.equals(type)) {
                                DomainEventMessage<?> evMsg = typedMessage.eventMessage;
                                if (identifier.equals(evMsg.getAggregateIdentifier())) {
                                    selection.add(typedMessage.eventMessage);
                                }
                            }
                        }

                        return new SimpleDomainEventStream(selection);
                    }
                }
        );
    }

    public PlatformWirer(
            final Config config,
            final MetricRegistry metricRegistry,
            final KasperEventBus eventBus,
            final KasperCommandGateway commandGateway,
            final KasperQueryGateway queryGateway,
            final SagaManager sagaManager,
            final RepositoryManager repositoryManager,
            final Meta meta,
            final EventStore eventStore
    ) {
        this.config = checkNotNull(config);
        this.metricRegistry = checkNotNull(metricRegistry);
        this.eventBus = checkNotNull(eventBus);
        this.commandGateway = checkNotNull(commandGateway);
        this.queryGateway = checkNotNull(queryGateway);
        this.sagaManager = checkNotNull(sagaManager);
        this.repositoryManager = checkNotNull(repositoryManager);
        this.domainDescriptorFactory = new DomainDescriptorFactory();
        this.extraComponents = Lists.newArrayList();
        this.registeredBundleNames = Sets.newHashSet();
        this.registeredPlugins = Maps.newHashMap();
        this.meta = checkNotNull(meta);
        this.eventStore = eventStore;
    }

    // ------------------------------------------------------------------------

    public void wire(final Plugin plugin) {
        checkState( ! registeredPlugins.containsKey(plugin.getName()), "Plugin name already wired : <name=%s>", plugin.getName());
        registeredPlugins.put(plugin.getName(), plugin);
        plugin.initialize(
                new PlatformContext(
                    config,
                    eventBus,
                    commandGateway,
                    queryGateway,
                    metricRegistry,
                    extraComponents,
                    meta
                )
        );
        firePluginRegistered(plugin);
    }

    public DomainDescriptor wire(final DomainBundle bundle) {
        checkState( ! registeredBundleNames.contains(bundle.getName()), "Bundle name already wired : <name=%s>", bundle.getName());
        bundle.configure(
                new PlatformContext(
                        config,
                        eventBus,
                        commandGateway,
                        queryGateway,
                        metricRegistry,
                        extraComponents,
                        meta
                )
        );

        for (final Repository repository : bundle.getRepositories()) {
            if (repository instanceof WirableRepository) {
                ((WirableRepository)repository).setEventBus(eventBus);
                ((WirableRepository)repository).setEventStore(eventStore);
            }
            repositoryManager.register(repository);
        }

        for (final CommandHandler commandHandler : bundle.getCommandHandlers()) {
            if (commandHandler instanceof WirableCommandHandler) {
                final WirableCommandHandler wirableCommandHandler = (WirableCommandHandler) commandHandler;
                wirableCommandHandler.setEventBus(eventBus);
                wirableCommandHandler.setRepositoryManager(repositoryManager);
                wirableCommandHandler.setCommandGateway(commandGateway);
            }

            commandGateway.register(new MeasuredCommandHandler(metricRegistry, commandHandler));
        }

        for (final QueryHandler queryHandler : bundle.getQueryHandlers()) {
            if (queryHandler instanceof WirableQueryHandler) {
                final WirableQueryHandler wirableQueryHandler = (WirableQueryHandler) queryHandler;
                wirableQueryHandler.setEventBus(eventBus);
                wirableQueryHandler.setQueryGateway(queryGateway);
            }

            queryGateway.register(new MeasuredQueryHandler(metricRegistry, queryHandler));
        }

        for (final EventListener eventListener : bundle.getEventListeners()) {
            if (eventListener instanceof WirableEventListener) {
                final WirableEventListener wirableEventListener = (WirableEventListener) eventListener;
                wirableEventListener.setEventBus(eventBus);

                if (CommandEventListener.class.isAssignableFrom(eventListener.getClass())) {
                    final CommandEventListener commandEventListener = (CommandEventListener) eventListener;
                    commandEventListener.setCommandGateway(commandGateway);
                }
            }

            eventBus.subscribe(eventListener);
        }

        for (final Saga saga : bundle.getSagas()) {
            final SagaExecutor<Saga> executor = sagaManager.register(saga);
            eventBus.subscribe(new SagaWrapper(executor));
        }

        for (final CommandInterceptorFactory factory : bundle.getCommandInterceptorFactories()) {
            wire(factory);
        }
        for (final QueryInterceptorFactory factory : bundle.getQueryInterceptorFactories()) {
            wire(factory);
        }

        for (final EventInterceptorFactory factory : bundle.getEventInterceptorFactories()) {
            wire(factory);
        }

        registeredBundleNames.add(bundle.getName());

        DomainDescriptor domainDescriptor = domainDescriptorFactory.createFrom(bundle);

        fireDomainBundleRegistered(domainDescriptor);

        return domainDescriptor;
    }

    public void wire(final CommandInterceptorFactory factory) {
        commandGateway.register(factory);
    }

    public void wire(final QueryInterceptorFactory factory) {
        queryGateway.register(factory);
    }

    public void wire(final EventInterceptorFactory factory) {
        eventBus.register(factory);
    }

    public void register(final ExtraComponent extraComponent) {
        checkNotNull(extraComponent);
        extraComponents.add(extraComponent);
    }

    @VisibleForTesting
    protected Collection<Plugin> sortRegisteredPlugins() {
        final List<Plugin> plugins = Lists.newArrayList(registeredPlugins.values());
        Collections.sort(plugins, Plugin.COMPARATOR);
        return plugins;
    }

    private void fireDomainBundleRegistered(final DomainDescriptor domainDescriptor) {
        checkNotNull(domainDescriptor);
        for (final Plugin plugin : sortRegisteredPlugins()) {
            plugin.onDomainRegistered(domainDescriptor);
        }
    }

    private void firePluginRegistered(final Plugin registeredPlugin) {
        checkNotNull(registeredPlugin);
        for (final Plugin plugin : sortRegisteredPlugins()) {
            plugin.onPluginRegistered(registeredPlugin);
        }
    }

}
