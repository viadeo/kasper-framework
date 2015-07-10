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
import com.viadeo.kasper.core.interceptor.EventInterceptorFactory;
import com.viadeo.kasper.core.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.api.domain.Domain;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.event.saga.Saga;
import com.viadeo.kasper.api.domain.exception.KasperException;
import org.springframework.beans.factory.BeanCreationException;

import java.util.Collection;
import java.util.List;

import static com.viadeo.kasper.client.platform.domain.DiscoveryDomainHelper.findComponents;

public class DiscoveryDomainBundle extends SpringDomainBundle {

    protected final Collection<Class<QueryHandler>> queryHandlerClasses;
    protected final Collection<Class<CommandHandler>> commandHandlerClasses;
    protected final Collection<Class<EventListener>> eventListenerClasses;
    protected final Collection<Class<Saga>> sagaClasses;
    protected final Collection<Class<Repository>> repositoryClasses;
    protected final Collection<Class<QueryInterceptorFactory>> queryInterceptorFactoryClasses;
    protected final Collection<Class<CommandInterceptorFactory>> commandInterceptorFactoryClasses;
    protected final Collection<Class<EventInterceptorFactory>> eventInterceptorFactoryClasses;

    // ------------------------------------------------------------------------

    public DiscoveryDomainBundle(final String basePackage, final List<Class> springConfigurations, final BeanDescriptor... beans) {
        this(
            basePackage,
            DiscoveryDomainHelper.findCandidateDomain(basePackage),
            springConfigurations,
            beans
        );
    }

    public DiscoveryDomainBundle(final String basePackage, final BeanDescriptor... beans) {
        this(
            basePackage,
            DiscoveryDomainHelper.findCandidateDomain(basePackage),
            Lists.<Class>newArrayList(),
            beans
        );
    }

    public DiscoveryDomainBundle(final String basePackage,
                                 final Domain domain,
                                 final List<Class> springConfigurations,
                                 final BeanDescriptor... beans) {

        super(domain, springConfigurations, beans);

        try {

            this.queryHandlerClasses = findComponents(basePackage, QueryHandler.class);
            this.commandHandlerClasses = findComponents(basePackage, CommandHandler.class);
            this.eventListenerClasses = findComponents(basePackage, EventListener.class);
            this.sagaClasses = findComponents(basePackage, Saga.class);
            this.repositoryClasses = findComponents(basePackage, Repository.class);
            this.queryInterceptorFactoryClasses = findComponents(basePackage, QueryInterceptorFactory.class);
            this.commandInterceptorFactoryClasses = findComponents(basePackage, CommandInterceptorFactory.class);
            this.eventInterceptorFactoryClasses = findComponents(basePackage, EventInterceptorFactory.class);

        } catch (final ReflectiveOperationException e) {
            throw new KasperException(String.format(
                "Unexpected error occurred while initializing the domain bundle with `%s` as base package",
                basePackage
            ));
        }
    }

    // ------------------------------------------------------------------------

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
            sagas.addAll(instantiate(componentsInstanceManager, sagaClasses));
            repositories.addAll(instantiate(componentsInstanceManager, repositoryClasses));
            queryInterceptorFactories.addAll(instantiate(componentsInstanceManager, queryInterceptorFactoryClasses));
            commandInterceptorFactories.addAll(instantiate(componentsInstanceManager, commandInterceptorFactoryClasses));
            eventInterceptorFactories.addAll(instantiate(componentsInstanceManager, eventInterceptorFactoryClasses));

        } catch (final ReflectiveOperationException e) {
            throw new KasperException("Unexpected error occurred while configuring the domain bundle : " + getName());
        }
    }

    // ------------------------------------------------------------------------

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
            } catch (final BeanCreationException e) {
                final COMP componentInstance = DiscoveryDomainHelper.instantiateComponent(applicationContext, componentClass);
                components.add(componentInstance);
                componentsInstanceManager.recordInstance(componentClass, componentInstance);
            }
        }

        return components;
    }

}
