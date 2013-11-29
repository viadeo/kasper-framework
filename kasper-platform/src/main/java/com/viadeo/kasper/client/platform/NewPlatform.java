package com.viadeo.kasper.client.platform;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.client.platform.domain.DomainBundle;
import com.viadeo.kasper.client.platform.domain.descriptor.DomainDescriptor;
import com.viadeo.kasper.client.platform.domain.descriptor.DomainDescriptorFactory;
import com.viadeo.kasper.client.platform.domain.descriptor.PlatformDescriptor;
import com.viadeo.kasper.client.platform.impl.DefaultNewPlatform;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.impl.DefaultCommandGateway;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.impl.DefaultQueryGateway;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.event.EventListener;

import java.util.Collection;
import java.util.Map;

public interface NewPlatform {

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

    public static class BuilderContext {

        private final Config configuration;
        private final KasperEventBus eventBus;
        private final CommandGateway commandGateway;
        private final QueryGateway queryGateway;

        public BuilderContext(Config configuration, KasperEventBus eventBus, CommandGateway commandGateway, QueryGateway queryGateway) {
            this.configuration = configuration;
            this.eventBus = eventBus;
            this.commandGateway = commandGateway;
            this.queryGateway = queryGateway;
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
    }

    public static class Builder{

        private final Collection<DomainBundle> domainBundles;
        private final Collection<Plugin> kasperPlugins;
        private final DomainDescriptorFactory domainDescriptorFactory;

        private KasperEventBus eventBus;
        private DefaultCommandGateway commandGateway;
        private DefaultQueryGateway queryGateway;
        private Config configuration;

        public Builder() {
            this(new DomainDescriptorFactory());
        }

        protected Builder(DomainDescriptorFactory domainDescriptorFactory) {
            this.domainDescriptorFactory = domainDescriptorFactory;
            this.domainBundles = Lists.newArrayList();
            this.kasperPlugins = Lists.newArrayList();
        }

        public Builder addDomainBundle(DomainBundle domainBundle) {
            this.domainBundles.add(Preconditions.checkNotNull(domainBundle));
            return this;
        }

        public Builder addPlugin(Plugin plugin) {
            this.kasperPlugins.add(Preconditions.checkNotNull(plugin));
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

        public NewPlatform build(){
            Preconditions.checkState(eventBus != null, "the event bus cannot be null");
            Preconditions.checkState(commandGateway != null, "the command gateway cannot be null");
            Preconditions.checkState(queryGateway != null, "the query gateway cannot be null");
            Preconditions.checkState(configuration != null, "the configuration cannot be null");

            BuilderContext context = new BuilderContext(configuration, eventBus, commandGateway, queryGateway);

            Map<String, DomainDescriptor> domainDescriptors = Maps.newHashMap();

            for (DomainBundle bundle : domainBundles) {
                bundle.configure(context);

                for(CommandHandler commandHandler : bundle.getCommandHandlers()){
                    commandHandler.setEventBus(eventBus);
                    commandGateway.register(commandHandler);
                }

                for(QueryHandler queryHandler: bundle.getQueryHandlers()){
                    queryGateway.register(queryHandler);
                }

                for(EventListener eventListener : bundle.getEventListeners()){
                    eventListener.setCommandGateway(commandGateway);
                    eventBus.subscribe(eventListener);
                }

                for(IRepository repository : bundle.getRepositories()){
                    repository.init();
                    if (Repository.class.isAssignableFrom(repository.getClass())) {
                        ((Repository) repository).setEventBus(eventBus);
                        commandGateway.register((Repository) repository);
                    }
                }

                domainDescriptors.put(bundle.getName(), domainDescriptorFactory.createFrom(bundle));
            }

            PlatformDescriptor platformDescriptor = new PlatformDescriptor(domainDescriptors);

            for(Plugin plugin : kasperPlugins){
                plugin.initialize(platformDescriptor);
            }

            return new DefaultNewPlatform(commandGateway, queryGateway, eventBus);
        }
    }
}
