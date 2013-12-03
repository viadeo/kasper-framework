package com.viadeo.kasper.client.platform;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.client.platform.domain.DomainBundle;
import com.viadeo.kasper.client.platform.domain.descriptor.DomainDescriptor;
import com.viadeo.kasper.client.platform.domain.descriptor.DomainDescriptorFactory;
import com.viadeo.kasper.client.platform.impl.DefaultPlatform;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.RepositoryManager;
import com.viadeo.kasper.cqrs.command.impl.DefaultCommandGateway;
import com.viadeo.kasper.cqrs.command.impl.DefaultRepositoryManager;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.impl.DefaultQueryGateway;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.event.EventListener;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Platform {

    /**
     * @return the Command gateway to use in order to send commands to the platform
     */
    CommandGateway getCommandGateway();

    /**
     * @return the query gateway to use in order to send queries to the platform
     */
    QueryGateway getQueryGateway();

    /**
     * @return the event bus used by the platform
     */
    KasperEventBus getEventBus();


    public static class Builder{

        private final Collection<DomainBundle> domainBundles;
        private final Collection<Plugin> kasperPlugins;
        private final Map<ExtraComponentKey, Object> extraComponents;
        private final DomainDescriptorFactory domainDescriptorFactory;

        private KasperEventBus eventBus;
        private DefaultCommandGateway commandGateway;
        private DefaultQueryGateway queryGateway;
        private Config configuration;
        private RepositoryManager repositoryManager;

        public Builder() {
            this(new DomainDescriptorFactory());
        }

        protected Builder(DomainDescriptorFactory domainDescriptorFactory) {
            this(domainDescriptorFactory, new DefaultRepositoryManager());
        }

        protected Builder(DomainDescriptorFactory domainDescriptorFactory, RepositoryManager repositoryManager) {
            this.domainDescriptorFactory = domainDescriptorFactory;
            this.repositoryManager = repositoryManager;
            this.domainBundles = Lists.newArrayList();
            this.kasperPlugins = Lists.newArrayList();
            this.extraComponents = Maps.newHashMap();
        }

        public Builder addDomainBundle(DomainBundle domainBundle) {
            this.domainBundles.add(Preconditions.checkNotNull(domainBundle));
            return this;
        }

        public Builder addPlugin(Plugin plugin) {
            this.kasperPlugins.add(Preconditions.checkNotNull(plugin));
            return this;
        }

        public <E> Builder addExtraComponent(String name, E component) {
            Preconditions.checkNotNull(name);
            Preconditions.checkNotNull(component);
            this.extraComponents.put(new ExtraComponentKey(name, component.getClass()), component);
            return this;
        }

        public Builder withConfiguration(Config configuration){
            this.configuration = Preconditions.checkNotNull(configuration);
            return this;
        }

        public Builder withEventBus(KasperEventBus eventBus) {
            this.eventBus = Preconditions.checkNotNull(eventBus);
            return this;
        }

        public Builder withCommandGateway(DefaultCommandGateway commandGateway) {
            this.commandGateway = Preconditions.checkNotNull(commandGateway);
            return this;
        }

        public Builder withQueryGateway(DefaultQueryGateway queryGateway) {
            this.queryGateway = Preconditions.checkNotNull(queryGateway);
            return this;
        }

        public Builder withRepositoryManager(RepositoryManager repositoryManager) {
            this.repositoryManager = Preconditions.checkNotNull(repositoryManager);
            return this;
        }

        public Platform build(){
            Preconditions.checkState(eventBus != null, "the event bus cannot be null");
            Preconditions.checkState(commandGateway != null, "the command gateway cannot be null");
            Preconditions.checkState(queryGateway != null, "the query gateway cannot be null");
            Preconditions.checkState(configuration != null, "the configuration cannot be null");
            Preconditions.checkState(repositoryManager != null, "the repository manager cannot be null");

            BuilderContext context = new BuilderContext(configuration, eventBus, commandGateway, queryGateway, extraComponents);

            List<DomainDescriptor> domainDescriptors = Lists.newArrayList();

            for (DomainBundle bundle : domainBundles) {
                bundle.configure(context);

                for(Repository repository : bundle.getRepositories()){
                    repository.init();
                    repository.setEventBus(eventBus);
                    repositoryManager.register(repository);
                }

                for(CommandHandler commandHandler : bundle.getCommandHandlers()){
                    commandHandler.setEventBus(eventBus);
                    commandHandler.setRepositoryManager(repositoryManager);
                    commandGateway.register(commandHandler);
                }

                for(QueryHandler queryHandler: bundle.getQueryHandlers()){
                    queryGateway.register(queryHandler);
                }

                for(EventListener eventListener : bundle.getEventListeners()){
                    eventListener.setCommandGateway(commandGateway);
                    eventBus.subscribe(eventListener);
                }

                domainDescriptors.add(domainDescriptorFactory.createFrom(bundle));
            }

            DefaultPlatform platform = new DefaultPlatform(commandGateway, queryGateway, eventBus);

            DomainDescriptor[] domainDescriptorArray = domainDescriptors.toArray(new DomainDescriptor[domainDescriptors.size()]);
            for(Plugin plugin : kasperPlugins){
                plugin.initialize(platform, domainDescriptorArray);
            }

            return platform;
        }
    }

    public static class BuilderContext {

        private final Config configuration;
        private final KasperEventBus eventBus;
        private final CommandGateway commandGateway;
        private final QueryGateway queryGateway;
        private final Map<ExtraComponentKey, Object> extraComponent;

        public BuilderContext(
                  Config configuration
                , KasperEventBus eventBus
                , CommandGateway commandGateway
                , QueryGateway queryGateway
                , Map<ExtraComponentKey, Object> extraComponent
        ) {
            this.configuration = configuration;
            this.eventBus = eventBus;
            this.commandGateway = commandGateway;
            this.queryGateway = queryGateway;
            this.extraComponent = extraComponent;
        }

        public Config getConfiguration() {
            return configuration;
        }

        public KasperEventBus getEventBus() {
            return eventBus;
        }

        public CommandGateway getCommandGateway() {
            return commandGateway;
        }

        public QueryGateway getQueryGateway() {
            return queryGateway;
        }

        @SuppressWarnings("unchecked")
        public <E> Optional<E> getExtraComponent(ExtraComponentKey key) {
            return Optional.fromNullable((E) extraComponent.get(key));
        }
    }

    public static class ExtraComponentKey {
        private final String name;
        private final Class clazz;

        public ExtraComponentKey(String name, Class clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        public Class getClazz() {
            return clazz;
        }

        public String getName() {
            return name;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, clazz);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final ExtraComponentKey other = (ExtraComponentKey) obj;
            return Objects.equal(this.name, other.name) && Objects.equal(this.clazz, other.clazz);
        }
    }
}
