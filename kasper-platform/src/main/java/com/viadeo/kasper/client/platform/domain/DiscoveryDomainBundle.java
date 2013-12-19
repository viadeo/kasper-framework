// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.domain;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.core.boot.SpringComponentsInstanceManager;
import com.viadeo.kasper.core.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.exception.KasperException;
import org.springframework.beans.factory.BeanCreationException;

import java.util.Collection;
import java.util.List;

public class DiscoveryDomainBundle extends SpringDomainBundle {

    protected final Collection<Class<QueryHandler>> queryHandlerClasses;
    protected final Collection<Class<CommandHandler>> commandHandlerClasses;
    protected final Collection<Class<EventListener>> eventListenerClasses;
    protected final Collection<Class<Repository>> repositoryClasses;
    protected final Collection<Class<QueryInterceptorFactory>> queryInterceptorFactoryClasses;
    protected final Collection<Class<CommandInterceptorFactory>> commandInterceptorFactoryClasses;

    public DiscoveryDomainBundle(final String basePackage, final List<Class> springConfigurations, final BeanDescriptor... beans) {
        this(basePackage, DiscoveryDomainHelper.findCandidateDomain(basePackage), springConfigurations, beans);
    }

    public DiscoveryDomainBundle(final String basePackage, final BeanDescriptor... beans) {
        this(basePackage, DiscoveryDomainHelper.findCandidateDomain(basePackage), Lists.<Class>newArrayList(), beans);
    }

    public DiscoveryDomainBundle(final String basePackage,
                                 final Domain domain,
                                 final List<Class> springConfigurations,
                                 final BeanDescriptor... beans) {
        super(domain, springConfigurations, beans);

        try {
            this.queryHandlerClasses = DiscoveryDomainHelper.findComponents(basePackage, QueryHandler.class);
            this.commandHandlerClasses = DiscoveryDomainHelper.findComponents(basePackage, CommandHandler.class);
            this.eventListenerClasses = DiscoveryDomainHelper.findComponents(basePackage, EventListener.class);
            this.repositoryClasses = DiscoveryDomainHelper.findComponents(basePackage, Repository.class);
            this.queryInterceptorFactoryClasses = DiscoveryDomainHelper.findComponents(basePackage, QueryInterceptorFactory.class);
            this.commandInterceptorFactoryClasses = DiscoveryDomainHelper.findComponents(basePackage, CommandInterceptorFactory.class);
        } catch (ReflectiveOperationException e) {
            throw new KasperException("Unexpected error occurred while initializing the domain bundle with `" + basePackage + "` as base package");
        }
    }

    @Override
    public void configure(Platform.BuilderContext context) {
        super.configure(context);

        final SpringComponentsInstanceManager componentsInstanceManager = new SpringComponentsInstanceManager();
        componentsInstanceManager.setApplicationContext(applicationContext);
        componentsInstanceManager.setBeansMustExists(false);

        try {
            commandHandlers.addAll(instantiate(componentsInstanceManager, commandHandlerClasses));
            queryHandlers.addAll(instantiate(componentsInstanceManager, queryHandlerClasses));
            eventListeners.addAll(instantiate(componentsInstanceManager, eventListenerClasses));
            repositories.addAll(instantiate(componentsInstanceManager, repositoryClasses));
            queryInterceptorFactories.addAll(instantiate(componentsInstanceManager, queryInterceptorFactoryClasses));
            commandInterceptorFactories.addAll(instantiate(componentsInstanceManager, commandInterceptorFactoryClasses));
        } catch (ReflectiveOperationException e) {
            throw new KasperException("Unexpected error occurred while configuring the domain bundle : " + getName());
        }
    }

    protected <COMP> Collection<COMP> instantiate(final SpringComponentsInstanceManager componentsInstanceManager,
                                                  final Collection<Class<COMP>> componentClasses)
            throws ReflectiveOperationException {
        final List<COMP> components = Lists.newArrayList();

        for (final Class<COMP> componentClass : componentClasses) {
            try {
                final Optional<COMP> instanceFromClass = componentsInstanceManager.getInstanceFromClass(componentClass);

                if (instanceFromClass.isPresent()) {
                    components.add(instanceFromClass.get());
                }
            } catch (BeanCreationException e) {
                COMP componentInstance = DiscoveryDomainHelper.instantiateComponent(applicationContext, componentClass);
                components.add(componentInstance);
                componentsInstanceManager.recordInstance(componentClass, componentInstance);
            }
        }

        return components;
    }
}
